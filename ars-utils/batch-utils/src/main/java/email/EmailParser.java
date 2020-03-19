package email;

import com.google.common.io.CharStreams;
import com.sun.mail.util.BASE64DecoderStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.*;

import utils.TikaConverter;

public class EmailParser {

    private static final Logger LOGGER = Logger.getLogger(EmailParser.class.getName());

    public Email parseEmail(String filePath) throws Exception {
        Email email = new Email();

        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        Session session = Session.getInstance(new Properties(), null);

        try {
            MimeMessage message = new MimeMessage(session, fileStream);
            ArrayList<Address> receivers = new ArrayList<>();
            ArrayList<Address> cc_list = new ArrayList<>();
            ArrayList<Address> bcc_list = new ArrayList<>();
            getReceivers(message, cc_list, Message.RecipientType.CC);
            getReceivers(message, bcc_list, Message.RecipientType.BCC);
            try {
                getReceivers(message, receivers, Message.RecipientType.TO);
            } catch (Exception e) {
                // TikaConverter.write("C:\\Users\\User\\Desktop\\TESTS\\emails_my\\failed_recipients_reading_message", file);
                LOGGER.log(Level.SEVERE, "Recipient:" + filePath, e);
                System.out.println();
            }
            email.setBccList(bcc_list);
            email.setReceiverList(receivers);
            email.setCcList(cc_list);

            email.setMessageText(CharStreams.toString(new InputStreamReader(message.getRawInputStream())));

            email.setDate(getOrDefault(message::getSentDate, null));
            email.addFrom(getOrDefault(message::getFrom, new Address[0]));
            email.addTo(getOrDefault(message::getAllRecipients, new Address[0]));
            email.setSubject(getOrDefault(message::getSubject, null));
            email.setAttachments(parseAttachments(message));
        } catch (MessagingException e) {
            throw e;
        }
        return email;
    }

    private <T> T getOrDefault(Callable<T> callable, T def) {
        try {
            return callable.call();
        } catch (Exception ignore) {
            return def;
        }
    }

    private void getReceivers(MimeMessage message, ArrayList<Address> contactArrayList, Message.RecipientType recipientType) throws MessagingException {
        try {
            if (message.getRecipients(recipientType) != null) {
                contactArrayList.addAll(Arrays.asList(message.getRecipients(recipientType)));
            }
        } catch (AddressException e) {
            LOGGER.log(Level.SEVERE, "Somethings ", e);
        }
    }


    private ArrayList<Attachment> parseAttachments(Message message) {
        ArrayList<Attachment> attachments = new ArrayList<>();
        try {
            Object content = message.getContent();
            if (content instanceof Multipart) {
                Multipart multi = (Multipart) content;
                for(int i = 0; i < multi.getCount(); i++) {
                    MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(i);
                    Attachment temp = new Attachment();

                    String fileName = part.getFileName();
                    if(StringUtils.isNotBlank(fileName)) {
                        temp.setFileExtension(FilenameUtils.getExtension(fileName));
                        temp.setName(FilenameUtils.getName(fileName));
                    } else {
                        temp.setFileExtension(null);
                        temp.setName(null);
                    }

                    if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        if(part.getContentType().contains("text/plain")) {
                            byte[] byteArray = part.getContent().toString().getBytes();
                            temp.setByteArray(byteArray);
                        }

                        if(part.getContent() instanceof BASE64DecoderStream) {
                            BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) part.getContent();
                            byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
                            temp.setByteArray(byteArray);
                        }
                    }

                    attachments.add(temp);
                }
            }
        } catch (IOException | MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error reading .eml message", e);
        }
        return attachments;
    }
}

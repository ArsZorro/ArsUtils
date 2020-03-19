package email;

import java.util.*;
import javax.mail.Address;

import com.google.common.collect.Sets;

public class Email {
    public Set<Address> from = new HashSet<>();
    public Set<Address> to = new HashSet<>();
    private Date date;
    private String messageText;
    private String subject;

    private ArrayList<Address> receiver_list = new ArrayList<>();
    private ArrayList<Address> cc_list = new ArrayList<>();
    private ArrayList<Address> bcc_list = new ArrayList<>();
    private ArrayList<Attachment> attachments = new ArrayList<>();

    public ArrayList<Address> getBcc_list() {
        return bcc_list;
    }

    public void setBccList(ArrayList<Address> bcc_list) {
        this.bcc_list = bcc_list;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public ArrayList<Address> getReceiverList() {
        return receiver_list;
    }

    public void setReceiverList(ArrayList<Address> receiver_list) {
        this.receiver_list = receiver_list;
    }

    public void setReceiver(Address receiver) {
        ArrayList<Address> single_receiver_list = new ArrayList<Address>();
        single_receiver_list.add(receiver);
        this.receiver_list = single_receiver_list;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Address> getCcList() {
        return cc_list;
    }

    public void setCcList(ArrayList<Address> cc_list) {
        this.cc_list = cc_list;
    }

    public void setCC(Address cc) {
        ArrayList<Address> single_cc = new ArrayList<Address>();
        single_cc.add(cc);
        this.cc_list = single_cc;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void addTo(Address[] to) {
        if (to != null) {
            this.to.addAll(Sets.newHashSet(to));
        }
    }

    public void addFrom(Address[] from) {
        if (from != null) {
            this.from.addAll(Sets.newHashSet(from));
        }
    }
}

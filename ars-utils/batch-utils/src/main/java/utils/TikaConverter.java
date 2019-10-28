package utils;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

@SuppressWarnings("AvoidModifiersForTypes")
public class TikaConverter {
    private static Logger logger = Logger.getLogger(TikaConverter.class);

    protected TikaConverter() {}

    public static String process(File file, String extension) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return process(stream, extension);
        }
    }

    @SuppressWarnings("MagicNumber")
    public static String process(FileInputStream stream, String extension) {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // Unlimited writing (-1)!
        Metadata metadata = new Metadata();

        ParseContext context = new ParseContext();
        context.set(Locale.class, Locale.forLanguageTag("rus"));

        TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
        ocrConfig.setLanguage("rus+eng");
        ocrConfig.setTimeout(300);
        context.set(TesseractOCRConfig.class, ocrConfig);

        byte[] byteArr = FileUtilsHelper.getByteArrayFromStream(stream);
        try (InputStream tikaStream = TikaInputStream.get(new ByteArrayInputStream(byteArr))) {
            parser.parse(tikaStream, handler, metadata, context);
        } catch (IOException | SAXException | TikaException e) {
            logger.error(String.format("The main Tika says:%n%s", e));
        }

        if (extension.matches("(?i)(pdf)")) {
            System.setProperty("pdfbox.fontcache", System.getProperty("java.io.tmpdir"));

            try (PDDocument document = PDDocument.load(new ByteArrayInputStream(byteArr))) {
                for (PDPage page : document.getPages()) {
                    PDResources pdResources = page.getResources();

                    for (COSName name : pdResources.getXObjectNames()) {
                        if (pdResources.getXObject(name) instanceof PDImageXObject) {
                            PDImageXObject img = (PDImageXObject) pdResources.getXObject(name);

                            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
                            ImageIOUtil.writeImage(img.getImage(), "jpg", imageOutputStream, 300);

                            try (InputStream tikaStream = TikaInputStream.get(new ByteArrayInputStream(imageOutputStream.toByteArray()))) {
                                parser.parse(tikaStream, handler, metadata, context);
                            } catch (IOException | SAXException | TikaException e) {
                                logger.error(String.format("The Tika for PDFs says:%n%s", e));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(String.format("The PDF section in the TikaConverter says:%n%s", e));
            }
        }
        return FileUtilsHelper.defaultNewLines(handler.toString());
    }
}

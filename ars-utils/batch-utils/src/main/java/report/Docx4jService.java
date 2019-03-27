package report;

import java.io.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class Docx4jService {
    public Logger logger = LogManager.getLogger(Docx4jService.class);

    public void convertFile(String path, String fileName) {
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(new File(path, fileName));
            Docx4J.save(wordprocessingMLPackage, new File(path, FilenameUtils.getBaseName(fileName) + System.currentTimeMillis() + ".xml"), Docx4J.FLAG_SAVE_FLAT_XML);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

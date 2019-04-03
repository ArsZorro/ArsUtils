package files;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

public class FilesUtilsHelper {
    public void createManyBigFiles(String path, String incomingFile, String resultFile, int startRowPosition, int maxLinesCounter) throws Exception {
        List<String> lines = FileUtils.readLines(
            new File(path + "\\" + incomingFile));

        StringBuilder stringBuilder = new StringBuilder();
        int rowCounter = 0;
        for (String line : lines) {
            if (rowCounter > startRowPosition + maxLinesCounter) {
                List<String> words = Arrays.asList(line.split("[,]"));
                for (int i = 0; i < words.size(); i++) {
                    String word = words.get(i);
                    if (i == 2) {
                        word = ("" + Math.abs(UUID.randomUUID().getLeastSignificantBits())).substring(0, 3);
                    }
                    if (i == 3) {
                        word = ("" + Math.abs(UUID.randomUUID().getLeastSignificantBits())).substring(0, 1);
                    }
                    if (i == 4) {
                        word = ("" + Math.abs(UUID.randomUUID().getLeastSignificantBits())).substring(0, 5);
                    }
                    if (i == 5) {
                        word = ("" + Math.abs(UUID.randomUUID().getLeastSignificantBits())).substring(0, 5);
                    }
                    stringBuilder.append(word).append(",");
                }
                if (rowCounter % 50_000 == 0) {
                    System.out.println(rowCounter);
                }
                stringBuilder.append("\n");
            }
            if (rowCounter == startRowPosition + maxLinesCounter) {
                break;
            }
            rowCounter++;
        }

        FileWriter fileWriter = new FileWriter(
            new File(path + "\\" + resultFile));
        fileWriter.append(stringBuilder.toString());
        fileWriter.flush();
    }
}

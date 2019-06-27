package report.support;

import java.math.BigInteger;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.SectPr;

public class PrintableReportUtils {
    private static final String LEFT_PAGE_INDENT = "1180";
    private static final String RIGHT_PAGE_INDENT = "571";

    private PrintableReportUtils() {}

    public static List<String> fixCsvLinesWithNewLineSeparators(List<String> csvLines, String newLineTempString) {
        if (csvLines.size() == 1 || csvLines.size() == 0) {
            return csvLines;
        }

        List<String> notEmptyLines = new ArrayList<>();
        int maxSplitterCount = 0;
        for (String line : csvLines) {
            if (!StringUtils.isEmpty(line.trim())) {
                int currentSplitterCount = getSplitterCount(line);
                maxSplitterCount = currentSplitterCount > maxSplitterCount ? currentSplitterCount : maxSplitterCount;
                notEmptyLines.add(line);
            }
        }

        List<String> fixedLines = new ArrayList<>();
        StringBuilder prevLine = new StringBuilder(notEmptyLines.get(0));
        for (int i = 1; i < notEmptyLines.size(); i++) {
            String currentLine = notEmptyLines.get(i);
            if (isSplitterCountLessOrTheSame(prevLine.toString(), currentLine, maxSplitterCount)) {
                if (isLastLine(i, notEmptyLines.size())) {
                    fixedLines.add(prevLine.toString() + newLineTempString + currentLine);
                } else {
                    prevLine.append(newLineTempString).append(currentLine);
                }
            } else {
                if (isLastLine(i, notEmptyLines.size())) {
                    fixedLines.add(prevLine.toString());
                    fixedLines.add(currentLine);
                } else {
                    fixedLines.add(prevLine.toString());
                    prevLine = new StringBuilder(currentLine);
                }
            }
        }

        return fixedLines;
    }

    private static int getSplitterCount(String line) {
        return line.split("\";\"").length;
    }

    private static boolean isSplitterCountLessOrTheSame(String firstLine, String secondLine, int maxSplitterCount) {
        return getSplitterCount(String.format("%s %s", firstLine, secondLine)) <= maxSplitterCount;
    }

    private static boolean isLastLine(int i, int size) {
        return i >= size - 1;
    }

    public static void processSectProperties(WordprocessingMLPackage wordMLPackage, ObjectFactory wmlObjectFactory) {
        SectPr sectPr = wordMLPackage.getDocumentModel().getSections().get(0).getSectPr();
        SectPr.PgMar pgMar = wmlObjectFactory.createSectPrPgMar();
        sectPr.setPgMar(pgMar);
        pgMar.setLeft(new BigInteger(LEFT_PAGE_INDENT));
        pgMar.setRight(new BigInteger(RIGHT_PAGE_INDENT));
    }

//    public static String getAsTimestampAndPrettify(Vertex vertex, String property) {
//        return prettifyTimestamp(getPropertyAsLong(vertex, property));
//    }
//
//    public static String getPropertyAsString(Vertex mainVertex, String property) {
//        Object object = mainVertex.property(property).orElse("");
//        return getObjectAsString(object);
//    }

    public static String getObjectAsString(Object object) {
        if (object instanceof String) {
            return (String) object;
        }

        if (object instanceof Number) {
            return object.toString();
        }

        return "";
    }
//
//    public static Long getPropertyAsLong(Vertex vertex, String property) {
//        Object vertexProperty = vertex.property(property).orElse(null);
//        return getObjectAsLong(vertexProperty);
//    }

    public static Long getObjectAsLong(Object object) {
        if (object == null) {
            return 0L;
        }

        if (object instanceof String) {
            try {
                return Long.parseLong((String) object);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }

        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        return 0L;
    }

    public static String prettifyTimestamp(Long time) {
        if (time == null || time.equals(0L)) {
            return "";
        }
        Format format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(new Date(time));
    }
}

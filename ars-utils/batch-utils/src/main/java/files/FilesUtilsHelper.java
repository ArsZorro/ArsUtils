package files;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.CharStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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


    public static String getStringFromClassPathFile(String fileName) {
        Resource resource = new DefaultResourceLoader().getResource("classpath:" + fileName);
        try {
            return getStringFromResource(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromResource(Resource resource) throws IOException {
        InputStream stream = resource.getInputStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, "UTF-8");
        return writer.toString();
    }

    public static List<String> readLinesFromResource(String filePath) throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:" + filePath);
        return CharStreams.readLines(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
    }

    public static boolean isValidRelativePath(String rootPath, String relativePath) {
        return new File(rootPath, relativePath)
                .toPath()
                .normalize()
                .startsWith(rootPath);
    }

    public static List<File> listFilesForFolder(File folder) {
        List<File> resultFiles = new ArrayList<>();
        try {
            Files.walk(folder.toPath())
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> resultFiles.add(new File(path.toString())));
            return resultFiles;
        } catch (IOException e) {
            return resultFiles;
        }
    }

    public static void removeFileOrFolderWithAllInnerFiles(String folderStringPath) throws IOException {
        if (!StringUtils.isEmpty(folderStringPath)) {
            File file = new File(folderStringPath);
            if (Files.isDirectory(file.toPath())) {
                FileUtils.cleanDirectory(file);
                Files.delete(file.toPath());
            } else {
                Files.delete(file.toPath());
            }
        }
    }

    private static List<File> getFilesRecurse(File dir, Pattern pattern, boolean rec, List<File> files) {
        File[] arr$ = dir.listFiles();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            File file = arr$[i$];
            if (file.isDirectory() && rec) {
                getFilesRecurse(file, pattern, rec, files);
            } else {
                Matcher m = pattern.matcher(file.getName());
                if (m.find()) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public static String fixFileName(String fileName) {
        String nameWithoutExtraSymbols = fileName.replaceAll("[|#$%&\'\"\\\\/\r\n\\[\\]*{}()^]+", "");
        String fixedName = nameWithoutExtraSymbols.split("[\\d][\\d][\\d][\\d][-][\\d][\\d][-][\\d][\\d]")[0];
        if (StringUtils.isEmpty(fixedName.trim())) {
            String newIdPrefix = String.format("%s", DigestUtils.md5Hex(fileName.getBytes()));
            return newIdPrefix + nameWithoutExtraSymbols;
        } else {
            return nameWithoutExtraSymbols;
        }
    }
}

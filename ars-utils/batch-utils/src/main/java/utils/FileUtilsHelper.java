package utils;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.DigestUtils;

public abstract class FileUtilsHelper {
    private static final Integer DEFAULT_BYTES_SIZE = 1024;

    public static void createManyBigFiles(String path, String incomingFile, String resultFile, int startRowPosition, int maxLinesCounter) throws Exception {
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

    private void saveFiles(List<File> files, File directory) {
        if (!directory.mkdirs() && !directory.exists()) {
            throw new RuntimeException("Cannot Saving files arrays");
        }

        for (File file : files) {
            File newFile = new File(directory.getAbsolutePath(), file.getName());
            newFile.mkdir();
            try {
                FileUtils.copyFile(file, newFile);
            } catch (IOException e) {
                System.out.println("Unable to common files " + file.getName());
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

    public static String getStringFromClassPathFile(String fileName) {
        try {
            return getStringFromResource(getClassPathResource(fileName));
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
        Resource resource = getClassPathResource(filePath);
        return CharStreams.readLines(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
    }

    public static boolean isValidRelativePath(String rootPath, String relativePath) {
        return new File(rootPath, relativePath)
            .toPath()
            .normalize()
            .startsWith(rootPath);
    }

    public static String fixFileName(String fileName) {
        String nameWithoutExtraSymbols = fileName.replaceAll("[|#$%&\'\"\\\\/\r\n\\[\\]*{}()^]+", "");
        String fixedName = nameWithoutExtraSymbols.split("[\\d][\\d][\\d][\\d][-][\\d][\\d][-][\\d][\\d]")[0];
        if (StringUtils.isEmpty(fixedName.trim())) {
            String newIdPrefix = String.format("%s", DigestUtils.md5DigestAsHex(fileName.getBytes()));
            return newIdPrefix + nameWithoutExtraSymbols;
        } else {
            return nameWithoutExtraSymbols;
        }
    }

    public static void saveFile(File fileTo, InputStream streamFrom) {
        final int bufferSize = 4096;
        try (FileOutputStream outputStream = new FileOutputStream(fileTo)) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = streamFrom.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> getFilesWithExtension(File directory, String extension) {
        if (directory == null || !directory.isDirectory() || (!directory.exists() && !directory.mkdir())) {
            return Collections.emptyList();
        }

        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                     .filter(file -> FilenameUtils.isExtension(file.getAbsolutePath(), extension))
                     .collect(Collectors.toList());
    }

    @SuppressWarnings("MagicNumber")
    public static byte[] getByteArrayFromStream(InputStream stream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BYTES_SIZE];
        int len;
        try {
            while ((len = stream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        } catch (IOException e) {
            return new byte[0];
        }

        return baos.toByteArray();
    }

    public static void copyResourceDirectoryToDestinationDirectory(String resourcePath, String destinationPath) throws IOException {
        Collection<File> listFilesAndDirsFromResources = getListFilesAndDirsFromResources(resourcePath);

        if (CollectionUtils.isEmpty(listFilesAndDirsFromResources)) {
            return;
        }

        File destinationFile = new File(destinationPath);
        if (!destinationFile.exists() && !destinationFile.mkdirs()) {
            return;
        }

        FileUtils.copyDirectory(listFilesAndDirsFromResources.iterator().next(), destinationFile);
    }

    public static Collection<File> getListFilesAndDirsFromResources(String directory) {
        try {
            String path = getAbsolutePathForResourceDirectory(directory);
            return FileUtils.listFilesAndDirs(new File(path), FileFileFilter.FILE, FileFileFilter.FILE);
        } catch (IOException ignore) {
            return Collections.emptyList();
        }
    }

    public static String getAbsolutePathForResourceDirectory(String directory) throws IOException {
        URL url = getClassPathResource(directory).getURL();
        return url.getPath();
    }

    public static File getClassPathFile(String filePath) {
        try {
            return getClassPathResource(filePath).getFile();
        } catch (IOException e) {
            return null;
        }
    }

    public static Resource getClassPathResource(String resourcePath) {
        return new DefaultResourceLoader().getResource("classpath:" + resourcePath);
    }

    public static String generateRandomDirectoryName() {
        Integer randomInt = RandomUtils.nextInt();
        return DigestUtils.md5DigestAsHex(randomInt.toString().getBytes());
    }

    public static File mkdirsIfNotExist(String directoriesPath) {
        File directory = new File(directoriesPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Unable to create directory: " + directoriesPath);
        }
        return directory;
    }

    public static List<File> getAllFilesWithAwaitingForEachFile(File directoryToSearch, int expectedFilesCount, Long timeAwaiting) {
        List<File> foundedFiles = new ArrayList<>();
        for (int i = 0; i < expectedFilesCount;) {
            List<File> newFiles = getNewFilesOrFinishByTimeout(foundedFiles, directoryToSearch, timeAwaiting);
            if (CollectionUtils.isEmpty(newFiles)) {
                i++;
            } else {
                foundedFiles.addAll(newFiles);
                i += newFiles.size();
            }
        }
        return foundedFiles;
    }

    public static List<File> getNewFilesOrFinishByTimeout(List<File> oldFiles, File reportDirectory, Long timeAwaiting) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<File> foundedFiles = Collections.emptyList();
        while (stopWatch.getTime() < timeAwaiting) {
            foundedFiles = listFilesFromFolder(reportDirectory);

            if (foundedFiles.size() > oldFiles.size()) {
                break;
            }
        }

        if (foundedFiles.size() > oldFiles.size()) {
            return getNewFiles(oldFiles, foundedFiles);
        } else {
            return Collections.emptyList();
        }
    }

    public static List<File> listFilesFromFolder(File folder) {
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

    public static List<File> getNewFiles(List<File> oldFiles, List<File> foundedFiles) {
        Map<String, File> oldFilesAbsolutePath2File = buildAbsolutePath2File(oldFiles);
        Map<String, File> foundedFilesAbsolutePath2File = buildAbsolutePath2File(foundedFiles);

        Set<String> newFilePaths = foundedFilesAbsolutePath2File.keySet();
        newFilePaths.removeAll(oldFilesAbsolutePath2File.keySet());

        if (CollectionUtils.isNotEmpty(newFilePaths)) {
            return CollectionsUtils.getValuesByKeys(foundedFilesAbsolutePath2File, newFilePaths);
        } else {
            return Collections.emptyList();
        }
    }

    public static Map<String, File> buildAbsolutePath2File(List<File> files) {
        Map<String, File> absolutePath2File = new HashMap<>();
        for (File file : files) {
            absolutePath2File.put(file.getAbsolutePath(), file);
        }
        return absolutePath2File;
    }

    public static String prettifyImageRecognizedText(String text) {
        return defaultNewLines(text).replaceAll("\n\n", "");
    }

    public static String defaultNewLines(String text) {
        return text.replaceAll("\r\n", "\n");
    }

    public static void copyResources(String from, String to) throws IOException {
        copyResources(from, to, "");
    }

    public static void copyResources(String from, String to, String extensionPattern) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                                     + from + "/**");
        URI inJarUri = new DefaultResourceLoader().getResource("classpath:" + from).getURI();

        for (Resource resource : resources) {
            String relativePath = resource
                .getURI()
                .getRawSchemeSpecificPart()
                .replace(inJarUri.getRawSchemeSpecificPart(), "");
            if (relativePath.isEmpty() || (!extensionPattern.isEmpty() && !relativePath.endsWith(extensionPattern))) {
                continue;
            }
            if (relativePath.endsWith("/") || relativePath.endsWith("\\")) {
                File dirFile = new File(to + relativePath);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                }
            } else {
                copyResourceToFilePath(resource, to + relativePath);
            }
        }
    }

    public static void copyResource(String source, String destination) throws IOException {
        Path destinationPath = Paths.get(destination);
        Files.createDirectories(destinationPath.getParent());
        Resource sourceResource = new DefaultResourceLoader().getResource("classpath:" + source);
        Files.copy(
            sourceResource.getInputStream(),
            destinationPath,
            StandardCopyOption.REPLACE_EXISTING);
    }

    private static void copyResourceToFilePath(Resource resource, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            InputStream resourceInputStream = resource.getInputStream();
            FileUtils.copyInputStreamToFile(resourceInputStream, file);
        }
    }

    public static String readFileByExtension(File inputFile) throws IOException {
        String text;
        String extension = FilenameUtils.getExtension(inputFile.toString()).toLowerCase();

        switch (extension) {
            case "txt" :
                text = FileUtils.readFileToString(inputFile, EncodingUtils.detectEncoding(inputFile.toString()));
                break;
            case "xlsx" :
            case "xls" :
            case "xlsm" :
            case "xlt" :
            case "xltm" :
                text = ExcelUtils.getText(inputFile);
                break;
            // case "docx" :
            // case "doc" :
                // text = readDocxFile(inputFile);
                // break;
                // try {
                //     StringBuilder stringBuilder = new StringBuilder();
                //     WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(inputFile);
                //     MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
                //     List<Object> paragraphs = mainDocumentPart.getContent();
                //     List<Object> allRuns = null;
                //     int k = 1;
                //     for (Object par : paragraphs) {
                //         if(par instanceof P) {
                //             P p = (P) par;
                //             // Get all the runs in the paragraph
                //             allRuns = p.getContent();
                //         }
                //         int i = 1;
                //         for (Object obj : allRuns) {
                //             if(obj instanceof R) {
                //                 List<Object> r = ((R)obj).getContent();
                //                 for (int j = 0; j < r.size(); ++j) {
                //                     javax.xml.bind.JAXBElement jaxb = (javax.xml.bind.JAXBElement) r.get(j);
                //                     Text t = (org.docx4j.wml.Text) jaxb.getValue();
                //                     stringBuilder.append(t.getValue()).append("\n");
                //                 }
                //             }
                //             i++;
                //         }
                //         k++;
                //     }
                //     text = stringBuilder.toString();
                //     break;
                // } catch (Docx4JException e) {
                //     throw new RuntimeException("File:" + inputFile.getAbsolutePath());
                // }
            default :
                text = TikaConverter.process(inputFile, extension);
                break;
        }

        return text;
    }

    public static boolean isApachePoiSupportedExcelFile(File file) {
        String extension = FilenameUtils.getExtension(file.toString()).toLowerCase();
        return Sets.newHashSet("xlsx", "xls", "xlsm", "xlt", "xltm").contains(extension);
    }

    public static String readDocxFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument document = new XWPFDocument(fis);
            for(int i = 0; i < document.getParagraphs().size(); i++){
                stringBuilder.append(document.getParagraphs().get(i).getParagraphText()).append("\n");
            }
            fis.close();
        } catch (Exception e) {
            throw new RuntimeException("File:" + file.getAbsolutePath(), e);
        }
        return stringBuilder.toString();
    }
}

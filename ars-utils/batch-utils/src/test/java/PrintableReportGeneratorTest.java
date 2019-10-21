import java.io.*;

import it.unimi.dsi.fastutil.chars.CharSets;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import report.generators.PrintableReportGenerator;
import report.support.PrintableReportParams;

public class PrintableReportGeneratorTest {
    private PrintableReportParams params;
    private String locale = "ru-RU";
    private String printableReportDirectory;

    private Long testRunningTime = System.currentTimeMillis();

    @Before
    public void init() {
        params = new PrintableReportParams();
        params.collapseLongSections = false;
        params.fontSize = 12;
        params.landscape = false;
        printableReportDirectory = "target/test";
        params.leftTopImagePath = getImageDirectory("leftTop.png");
        params.rightTopImagePath = getImageDirectory("rightTop.png");
    }

    private String getImageDirectory(String pngFileName) {
//        try {
////            return FileUtilsHelper.getAbsolutePathForResourceDirectory("report/" + pngFileName);
//        } catch (IOException e) {
//            return "";
//        }
        return "";
    }

    @Test
    public void testGeneratorOrg() throws Exception {
        String orgFileAndDirectoryName = "test_org";

        prepareReportData(orgFileAndDirectoryName);

        File reportFile = generateReport(orgFileAndDirectoryName);

        doAsserting(reportFile);
    }

    @Test
    public void testGeneratorPerson() throws Exception {
        String personFileAndDirectoryName = "test_person";

        prepareReportData(personFileAndDirectoryName);

        File reportFile = generateReport(personFileAndDirectoryName);

        doAsserting(reportFile);
    }

    @Test
    public void testHtml() throws Exception {
        String personFileAndDirectoryName = "test_person";

        prepareReportData(personFileAndDirectoryName);

        PrintableReportGenerator generator = new PrintableReportGenerator(params, locale);
        File reportFile = new File(getReportFilePath(personFileAndDirectoryName));
        generator.generateByHtml(new File("C:\\projects\\ArsUtils\\ars-utils\\batch-utils\\src\\test\\resources\\report\\result.docx"), new File("C:\\projects\\ArsUtils\\ars-utils\\batch-utils\\src\\test\\resources\\report\\te.html"));
        // return reportFile;

//        withConvertationToXML(reportFile);
        // doAsserting(reportFile);
    }

    @Test
    public void justDoConvertation() {
        withConvertationToXML(new File("C:\\projects\\projects_for_fast_help\\Octopus\\octopus-webapp\\target\\octopus\\printable-report\\test_person1561632769706.docx"));
    }

    @Test
    public void testJsoup() throws IOException {
        String htmlString = "<html lang=\"ru\">\n"
                            + "<head>\n"
                            + "    <META http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\">\n"
                            + "    <title>Запрос 1489b9fc-2060-4e6c-a211-c06be4150501</title>\n"
                            + "    <meta charset=\"utf-8\">\n"
                            + "    <style type=\"text/css\"> html, body, address, blockquote, div, dl, form, h1, h2, h3, h4, h5, h6, ol, p, pre, table, ul, dd, dt, li, tbody, td, tfoot, th, thead, tr, btn, del, ins, map, object, a, awbr, acronym, b, bdo, big, br, cite, code, dfn, em, i, img, kbd, q, samp, small, span, strong, sub, sup, tt, var, legend, fieldset {\n"
                            + "        .bg-warning {\n"
                            + "            color: #d79c00;\n"
                            + "\n"
                            + "        }\n"
                            + "\n"
                            + "\n"
                            + "     </style>\n"
                            + "\n"
                            + "</head>\n\n"
                            + "<body>\n"
                            + "<div id=\"page\">\n"
                            + "    <div id=\"p-content\">\n"
                            + "        <div id=\"header\" class=\"\">\n"
                            + "            <div class=\"mid\">\n"
                            + "                <div>\n"
                            + "                    <div id=\"logo\"><i id=\"logoImg\" class=\"sprAnswer\"></i></div>\n"
                            + "\n"
                            + "                    <div id=\"mainMenu\">\n"
                            + "                        <div id=\"menu-wrapper\">\n"
                            + "                            <button id=\"menuToggle\" class=\"menu-content\" disabled><span>Содержание</span>\n"
                            + "                                <div id=\"menu-addOn\"><i class=\"sprAnswer spr-menuToggle\"></i></div>\n"
                            + "                            </button>\n"
                            + "                            <div id=\"menu\" class=\"hide\">\n"
                            + "                                <ul></ul>\n"
                            + "\n"
                            + "                            </div>\n"
                            + "\n"
                            + "                        </div>\n"
                            + "                        <button id=\"rtf\" disabled><i class=\"sprAnswer spr-word\"></i></button>\n"
                            + "                    </div>\n"
                            + "\n"
                            + "                </div>\n"
                            + "\n"
                            + "            </div>\n"
                            + "\n"
                            + "        </div>"
                            + "\n"
                            + "     </div>"
                            + "\n"
                            + "</div>"
                            + "<div class=\"js_section\"><h2>АФФИЛИРОВАННОСТЬ</h2><h3 data-count=\"27\" title=\"БЕЛЯЕВА НАТАЛИЯ ПЕТРОВНА\"><i\n"
                            + "                            class=\"bookmark animated hide\" id=\"affFirstPerson\"></i>Аффилированные структуры, в которых Беляева Н. П.:</h3>\n"
                            + "                        <div class=\"tableWrapper\">\n"
                            + "                            <table class=\"table affiliation\">\n"
                            + "                            <thead>\n"
                            + "                            <tr>\n"
                            + "                                <th class=\"numered text-center\">№</th>\n"
                            + "                                <th>Категория аффилированности</th>\n"
                            + "\n"
                            + "                                <th class=\"linked text-center\">Кол-во</th>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            </thead>\n"
                            + "\n"
                            + "                            <tbody>\n"
                            + "                            <tr>\n"
                            + "                                <th colspan=\"3\">точная идентификация лица</th>\n"
                            + "\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">1.</td>\n"
                            + "                                <td>В настоящее время входит в состав учредителей ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\">0</td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">2.</td>\n"
                            + "                                <td>Ранее входила в состав учредителей ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\"><a href=\"#earlyFounderOrg\">1</a></td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">3.</td>\n"
                            + "                                <td>В настоящее время является руководителем ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\">0</td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">4.</td>\n"
                            + "                                <td>Ранее руководила ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\"><a href=\"#earlyDirectorOrg\">1</a></td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">5.</td>\n"
                            + "                                <td>Зарегистрирована в качестве ИНДИВИДУАЛЬНОГО ПРЕДПРИНИМАТЕЛЯ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\"><a href=\"#founderIP\">1</a></td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <th colspan=\"3\">идентификация лица только по ФИО</th>\n"
                            + "\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">6.</td>\n"
                            + "                                <td>Полные однофамильцы входят / ранее входили в состав УЧРЕДИТЕЛЕЙ ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\"><a href=\"#fioOnlyFounderOrg\">14</a></td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            <tr>\n"
                            + "                                <td class=\"numered text-center\">7.</td>\n"
                            + "                                <td>Полные однофамильцы являются / ранее являлись РУКОВОДИТЕЛЕМ ЮЛ</td>\n"
                            + "\n"
                            + "                                <td class=\"linked text-center\"><a href=\"#fioOnlyDirectorOrg\">10</a></td>\n"
                            + "                            </tr>\n"
                            + "\n"
                            + "                            </tbody>\n"
                            + "                            </table>\n"
                            + "                        </div>\n"
                            + "                        <h6>Известна также как: Беляева Наталья Петровна.</h6><h5>Беляева Наталия Петровна, подробнее об аффилированных\n"
                            + "                            структурах:</h5>\n"
                            + "                        <ol class=\"unstyled custom_numbering\">\n"
                            + "                        <li><i class=\"bookmark animated hide\" id=\"earlyFounderOrg\"></i>\n"
                            + "                            <div>2.</div>\n"
                            + "\n"
                            + "                            <div class=\"list-offset\"><strong>Беляева Н. П. ранее входила в состав учредителей ЮЛ:</strong>\n"
                            + "                                <ol class=\"unstyled custom_numbering\">\n"
                            + "                        <li>\n"
                            + "                            <div>2.1.</div>\n"
                            + "\n"
                            + "                            <div class=\"list-offset\"><i class=\"bookmark animated hide\" id=\"aff_1057748051514\"></i><p><strong>ООО\n"
                            + "                                \"РИО-ЛИТ\"</strong></p><p>ОГРН/ИНН: 1057748051514/7716532087</p><p>Статус: ПРЕКРАЩЕНИЕ ДЕЯТЕЛЬНОСТИ ЮЛ В СВЯЗИ С\n"
                            + "                                ИСКЛЮЧЕНИЕМ ИЗ ЕГРЮЛ НА ОСНОВАНИИ П.2 СТ.21.1 ФЗ ОТ 08.08.2001 №129-ФЗ</p><p>Дата прекращения деятельности:\n"
                            + "                                01.09.2014 г.</p><p>Дата регистрации: 31.08.2005 г.</p><p>Адрес: 129344, МОСКВА ВЕРХОЯНСКАЯ 10</p><p><span>Основной вид деятельности: </span><span>ИЗДАТЕЛЬСКАЯ ДЕЯТЕЛЬНОСТЬ</span><span\n"
                            + "                                    class=\"text-muted\"> (22.1)</span></p><p><span>Первое лицо ФИО: </span><span></span><u>БЕЛЯЕВА НАТАЛИЯ\n"
                            + "                                ПЕТРОВНА</u><span></span> (ИНН: 771507758936)</p><p>\n"
                            + "                                <span>Первое лицо должность: </span><span>Генеральный директор</span></p><p>Уставный капитал: 10'000.00 руб.</p>\n"
                            + "                                <p>Учредители:</p>\n"
                            + "                                <ul>\n"
                            + "                                    <li><span><u>Беляева Наталия Петровна</u> (ИНН: 771507758936), вклад - 10'000 руб. (100.00 %)</span></li>\n"
                            + "\n"
                            + "                                </ul>\n"
                            + "\n"
                            + "                            </div>\n"
                            + "\n"
                            + "                        </li>\n"
                            + "                        </ol>\n"
                            + "                    </div>"
                            + "</body>\n"
                            + "\n"
                            + "</html>";

        String te = "<ol class=\"unstyled custom_numbering\">\n" +
                "                                    <li> 2.1. <i class=\"bookmark animated hide\" id=\"aff_1057748051514\"></i><p><strong>ООО \"РИО-ЛИТ\"</strong></p><p>ОГРН/ИНН: 1057748051514/7716532087</p><p>Статус: ПРЕКРАЩЕНИЕ ДЕЯТЕЛЬНОСТИ ЮЛ В СВЯЗИ С ИСКЛЮЧЕНИЕМ ИЗ ЕГРЮЛ НА ОСНОВАНИИ П.2 СТ.21.1 ФЗ ОТ 08.08.2001 №129-ФЗ</p><p>Дата прекращения деятельности: 01.09.2014 г.</p><p>Дата регистрации: 31.08.2005 г.</p><p>Адрес: 129344, МОСКВА ВЕРХОЯНСКАЯ 10</p><p><span>Основной вид деятельности: </span><span>ИЗДАТЕЛЬСКАЯ ДЕЯТЕЛЬНОСТЬ</span><span class=\"text-muted\"> (22.1)</span></p><p><span>Первое лицо ФИО: </span><span></span><u>БЕЛЯЕВА НАТАЛИЯ ПЕТРОВНА</u><span></span> (ИНН: 771507758936)</p><p> <span>Первое лицо должность: </span><span>Генеральный директор</span></p><p>Уставный капитал: 10'000.00 руб.</p> <p>Учредители:</p>\n" +
                "                                        <ul>\n" +
                "                                            <li><span><u>Беляева Наталия Петровна</u> (ИНН: 771507758936), вклад - 10'000 руб. (100.00 %)</span></li>\n" +
                "                                        </ul>  </li>\n" +
                "                                </ol>";
        String v = IOUtils.toString(new FileInputStream(new File("C:\\projects\\ArsUtils\\ars-utils\\batch-utils\\src\\test\\resources\\report\\widget.html")), "WINDOWS-1251");
        Document doc = Jsoup.parse(v);

        htmlCleanElementsFromTag(doc, "div[class=list-offset]");
        htmlCleanElementsFromTag(doc, "ol[class=unstyled custom_numbering]");
        htmlReplaceElementsTagAndAttributes(doc, "div[class=list-number]", "strong", PARAGRAPH_NUMBER_REGEXP);
        htmlCleanElementsFromTag(doc, "a[href]", PARAGRAPH_NUMBER_REGEXP);
        htmlCleanElementsFromTag(doc, "div", PARAGRAPH_NUMBER_REGEXP);
        htmlCleanElementsParentTag(doc, "p > strong");
        htmlRemoveFullElement(doc, "span", "содержание");

        System.out.println(doc.html());
    }

    private void htmlRemoveFullElement(Document doc, String tagPattern, String searchedElementText) {
        Elements elements = doc.select(tagPattern);
        for (Element element : elements) {
            String elementText = element.text();
            if (elementText.trim().toUpperCase().equals(searchedElementText.toUpperCase())) {
                element.remove();
            }
        }
    }

    private void htmlReplaceElementsTagAndAttributes(Document doc, String tagPattern, String newTag, String regexp) {
        Elements elements = doc.select(tagPattern);
        for (Element element : elements) {
            String elementText = element.text();
            if (elementText.matches(regexp)) {
                element.tagName(newTag);
                element.clearAttributes();
            }
        }
    }

    private void htmlCleanElementsParentTag(Document doc, String tagPattern) {
        Elements elements = doc.select(tagPattern);
        for (Element element : elements) {
            element.parent().unwrap();
        }
    }

    private static final String PARAGRAPH_NUMBER_REGEXP = "[ \t\n]*[\\d\\.\\(\\)]+[ \t\n]*";

    @Test
    public void testRegexp() {
        System.out.println("2.1.".matches(PARAGRAPH_NUMBER_REGEXP));
    }

    private void htmlCleanElementsFromTag(Document doc, String tagPattern, String regexp) {
        Elements elements = doc.select(tagPattern);
        for (Element element : elements) {
            String elementText = element.text();
            if (regexp == null || elementText.matches(regexp)) {
                element.unwrap();
            }
        }
    }

    private void htmlCleanElementsFromTag(Document doc, String tagPattern) {
        htmlCleanElementsFromTag(doc, tagPattern, null);
    }

    public void withConvertationToXML(File docxFile) {
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxFile);
            Docx4J.save(wordMLPackage, getXMLFileFromDocx(docxFile), Docx4J.FLAG_SAVE_FLAT_XML);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareReportData(String reportFileAndDirectoryName) throws IOException {
        String processedDirectoryPath = getReportInnerDirectoryPath(reportFileAndDirectoryName);
//        FileUtilsHelper.copyResourceDirectoryToDestinationDirectory("report/" + reportFileAndDirectoryName, processedDirectoryPath);
    }

    private File generateReport(String reportFileAndDirectoryName) throws Exception {
        PrintableReportGenerator generator = new PrintableReportGenerator(params, locale);
        String processedDirectoryPath = getReportInnerDirectoryPath(reportFileAndDirectoryName);
        File reportFile = new File(getReportFilePath(reportFileAndDirectoryName));
        generator.generate(reportFile, new File(processedDirectoryPath));
        return reportFile;
    }

    private void doAsserting(File reportFile) throws IOException {
        Assert.assertTrue("File not exist: " + reportFile.getAbsolutePath(), reportFile.exists());
        byte[] reportBytes = IOUtils.toByteArray(new FileInputStream(reportFile));
//        Assert.assertTrue("Report is empty: ", ArrayUtils.isNotEmpty(reportBytes));
    }

    private String getReportInnerDirectoryPath(String reportDataDirectory) {
        return printableReportDirectory + File.separator + reportDataDirectory;
    }

    private String getReportFilePath(String fileName) {
        return getFilePath(fileName, ".docx");
    }

    private File getXMLFileFromDocx(File file) {
        return new File(getFilePath(FilenameUtils.getBaseName(file.getName().replaceAll("\\d+", "")), ".xml"));
    }

    private String getFilePath(String fileName, String extension) {
        return printableReportDirectory + File.separator + fileName + testRunningTime + extension;
    }

    @Test
    public void te() throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

//        mdp.addParagraphOfText("Paragraph 1");

        // Add the XHTML altChunk
        File file = new File("C:\\projects\\ArsUtils\\ars-utils\\batch-utils\\src\\test\\resources\\report\\widget.html");
        String s = FileUtils.readFileToString(file, "windows-1251");
//        String xhtml = "<html><head><title>Import me</title></head><body><p>Hello World!</p></body></html>";
        mdp.addAltChunk(AltChunkType.Xhtml, s.getBytes());

        mdp.addParagraphOfText("Paragraph 3");

        // Round trip
        WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

//        // Display result
//        System.out.println(
//                XmlUtils.marshaltoString(pkgOut.getMainDocumentPart().getJaxbElement(), true, true));

        pkgOut.save(new java.io.File(System.getProperty("user.dir") + "/Desktop/" + System.currentTimeMillis() + "html_2_doc.docx"));
    }
}

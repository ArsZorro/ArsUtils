package report.generators;

import java.io.*;
import java.math.BigInteger;
import static java.nio.charset.StandardCharsets.*;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.dml.CTPositiveSize2D;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import report.support.PrintableReportParams;
import report.support.PrintableReportUtils;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class PrintableReportGenerator {
    private static final Integer FONT_SIZE_MULTIPLIER = 2;
    private static final Integer WIDGET_TABLE_FONT_SIZE = 16;
    private static final Integer DEFAULT_PAGE_SIZE = 10000;
    private static final Integer MAX_CELL_TEXT_LENGTH_TO_COLLAPSE = 1000;
    private static final Integer WIDGET_ROWS_COUNT_FOR_COLLAPSE = 20;
    private static final Long RESIZE_IMAGE_Y_NUMBER = 333375L;
    private static final Long RESIZE_IMAGE_Y_DELTA = 500L;
    private static final Double COMPRESS_IMAGE_PERCENTS = 0.6;
    private static final String DEFAULT_FONT_NAME = "Times New Roman";
    private static final String DEFAULT_FONT_COLOR = "000000";
    private static final String CELL_MARGIN_LEFT = "10";
    private static final String CELL_MARGIN_RIGHT = "40";
    private static final String CELL_MARGIN_TOP = "100";
    private static final String CELL_MARGIN_BOTTOM = "40";
    private static final String DISTANCE_BETWEEN_TEXT_LINES = "40";
    private static final String DEFAULT_TABLE_WIDTH_UNITS = "dxa";
    private static final String TABLE_ROW_COLOR = "D3D3D3";
    private static final String TABLE_TOP_COLOR = "9A9AF9";
    private static final String NEW_LINE_TEMP_STRING = "TMP_NEW_LINE";
    private static ObjectFactory wmlObjectFactory;
    private WordprocessingMLPackage wordMLPackage;
    private MainDocumentPart mdp;
    private Integer defaultFontSize;
    private boolean collapseLongSections;
    private Integer imageCounter;
    private Logger logger = Logger.getLogger(getClass());
    private String leftTopImagePath;
    private String rightTopImagePath;
    private String docDefaultLang;

    public PrintableReportGenerator(PrintableReportParams params, String locale) throws InvalidFormatException {
        String paperSize = Docx4jProperties.getProperties().getProperty("docx4j.PageSize", "A4");
        wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.valueOf(paperSize), params.landscape);
        mdp = wordMLPackage.getMainDocumentPart();
        wmlObjectFactory = Context.getWmlObjectFactory();
        defaultFontSize = params.fontSize * FONT_SIZE_MULTIPLIER;
        collapseLongSections = params.collapseLongSections;
        imageCounter = 0;
        docDefaultLang = locale;
        alterStyleSheet();

        if (params.leftTopImagePath != null) {
            this.leftTopImagePath = params.leftTopImagePath;
        } else {
            this.leftTopImagePath = "";
        }
        if (params.rightTopImagePath != null) {
            this.rightTopImagePath = params.rightTopImagePath;
        } else {
            this.rightTopImagePath = "";
        }
    }

    private void alterStyleSheet() {
        StyleDefinitionsPart styleDefinitionsPart = mdp.getStyleDefinitionsPart();
        Styles styles = styleDefinitionsPart.getJaxbElement();

        List<Style> stylesList = styles.getStyle();
        for (Style style : stylesList) {
            if (style.getStyleId().equals("Normal")) {
                alterNormalStyle(style, false);
            } else if (style.getStyleId().equals("Title")) {
                alterTitleStyle(style);
            } else {
                getRunPropertiesAndRemoveThemeInfo(style);
            }
        }

        styles.setDocDefaults(createDocDefaults(styles));
    }

    private DocDefaults createDocDefaults(Styles styles) {
        CTLanguage ctLanguage = new CTLanguage();
        ctLanguage.setBidi(docDefaultLang);
        ctLanguage.setEastAsia(docDefaultLang);
        ctLanguage.setVal(docDefaultLang);

        DocDefaults docDefaults = styles.getDocDefaults();
        DocDefaults.RPrDefault rprDefault = docDefaults.getRPrDefault();
        RPr rpr = rprDefault.getRPr();
        rpr.setLang(ctLanguage);
        return docDefaults;
    }

    private void alterNormalStyle(Style style, boolean addBold) {
        RPr rpr = new RPr();
        changeFont(rpr);
        changeFontColor(rpr);
        changeFontSize(rpr, defaultFontSize);

        if (addBold) {
            BooleanDefaultTrue b = new BooleanDefaultTrue();
            b.setVal(true);
            rpr.setB(b);
        }

        style.setRPr(rpr);
    }

    private void alterTitleStyle(Style style) {
        alterNormalStyle(style, true);

        PPr paragraphProperties = wmlObjectFactory.createPPr();
        paragraphProperties.setJc(createJustification(JcEnumeration.CENTER));
        style.setPPr(paragraphProperties);
    }

    private static void getRunPropertiesAndRemoveThemeInfo(Style style) {
        RPr rpr = style.getRPr();
        if (rpr != null) {
            changeFontColor(rpr);
            removeThemeFontInformation(rpr);
        }
    }

    private static void changeFont(RPr runProperties) {
        RFonts runFont = new RFonts();
        runFont.setAscii(DEFAULT_FONT_NAME);
        runFont.setHAnsi(DEFAULT_FONT_NAME);
        runProperties.setRFonts(runFont);
    }

    private static void changeFontColor(RPr runProperties) {
        Color color = new Color();
        color.setVal(DEFAULT_FONT_COLOR);
        runProperties.setColor(color);
    }

    private void changeFontSize(RPr runProperties, int fontSize) {
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(fontSize));
        runProperties.setSz(size);
    }

    private static void changeBold(RPr runProperties, boolean isHeader) {
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        b.setVal(isHeader);
        runProperties.setB(b);
    }

    private static void removeThemeFontInformation(RPr runProperties) {
        RFonts rfonts = runProperties.getRFonts();
        if (rfonts == null) {
            return;
        }

        runProperties.getRFonts().setAsciiTheme(null);
        runProperties.getRFonts().setHAnsiTheme(null);
    }

    public void generate(File reportFile, File reportDirectory) throws Exception {
        PrintableReportUtils.processSectProperties(wordMLPackage, wmlObjectFactory);
        processTopPngData();
        processDirectory(reportDirectory);
        Docx4J.save(wordMLPackage, reportFile, Docx4J.FLAG_SAVE_ZIP_FILE);
    }

    public void generateByHtml(File reportFile, File processedHtml) throws Exception {
        PrintableReportUtils.processSectProperties(wordMLPackage, wmlObjectFactory);
        // processTopPngData();
        processHtml(processedHtml);
        Docx4J.save(wordMLPackage, reportFile, Docx4J.FLAG_SAVE_ZIP_FILE);
    }

    private void processDirectory(File reportDirectory) throws Exception {
        processTitle(reportDirectory);
        processHeader(reportDirectory);
        processDate(reportDirectory);
        processTxtData(reportDirectory);
        processExtendedTitle(reportDirectory);
        processProperties(reportDirectory);
        processWidget(reportDirectory);
        processPngData(reportDirectory);

        File[] subDirectories = reportDirectory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        if (subDirectories == null) {
            return;
        }
        Arrays.sort(subDirectories, (o1, o2) -> {
            Integer numeralFilename1 = Integer.valueOf(FilenameUtils.getBaseName(o1.getName()));
            Integer numeralFilename2 = Integer.valueOf(FilenameUtils.getBaseName(o2.getName()));
            return numeralFilename1.compareTo(numeralFilename2);
        });
        for (File subDirectory : subDirectories) {
            processDirectory(subDirectory);
        }
    }

    private boolean isWidgetCollapsible(File reportDirectory) {
        File widgetsFile = new File(reportDirectory, "widget.csv");
        if (widgetsFile.exists()) {
            List<String> csvLines;
            try {
                csvLines = FileUtils.readLines(widgetsFile, "UTF-8");
            } catch (IOException e) {
                return false;
            }
            return csvLines.size() > WIDGET_ROWS_COUNT_FOR_COLLAPSE;
        }
        return false;
    }

    private void processTitle(File reportDirectory) throws Exception {
        File titleFile = new File(reportDirectory, "title.txt");
        if (titleFile.exists()) {
            mdp.addStyledParagraphOfText("Title", FileUtils.readFileToString(titleFile, "UTF-8"));
        }
    }

    private void processHeader(File reportDirectory) throws Exception {
        File headerFile = new File(reportDirectory, "header.txt");
        if (headerFile.exists()) {
            P p = mdp.createStyledParagraphOfText("Heading3", FileUtils.readFileToString(headerFile, "UTF-8"));
            if (isWidgetCollapsible(reportDirectory)) {
                setCollapsed(p);
            }
            mdp.addObject(p);
        }
    }

    private void processDate(File reportDirectory) throws Exception {
        File dateFile = new File(reportDirectory, "date.txt");
        if (dateFile.exists()) {
            P p = wmlObjectFactory.createP();
            PPr paragraphProperties = getParagraphPr(p);
            paragraphProperties.setJc(createJustification(JcEnumeration.RIGHT));

            Br br = wmlObjectFactory.createBr();
            p.getContent().add(br);

            String date = FileUtils.readFileToString(dateFile, "UTF-8");
            addLineToParagraph(p, date);
            mdp.getContent().add(p);
        }
    }

    private void processTxtData(File reportDirectory) throws Exception {
        File txtDataFile = new File(reportDirectory, "data.txt");
        if (txtDataFile.exists()) {
            List<String> lines = FileUtils.readLines(txtDataFile, "UTF-8");
            P p = wmlObjectFactory.createP();

            Integer lineIndex = 0;
            Integer lastLineIndex = lines.size() - 1;
            for (String line : lines) {
                addLineToParagraph(p, line);

                if (lineIndex < lastLineIndex) {
                    Br br = wmlObjectFactory.createBr();
                    p.getContent().add(br);
                }

                lineIndex++;
            }
            mdp.getContent().add(p);
        }
    }

    private void processExtendedTitle(File reportDirectory) throws Exception {
        processCsvData(reportDirectory, "extendedTitle.csv", false, false, defaultFontSize, false);
    }

    private void processProperties(File reportDirectory) throws Exception {
        processCsvData(reportDirectory, "properties.csv", false, false, defaultFontSize, false);
    }

    private void processWidget(File reportDirectory) throws Exception {
        processCsvData(reportDirectory, "widget.csv", true, true, WIDGET_TABLE_FONT_SIZE, true);
    }

    Charset W_1 = Charset.forName("WINDOWS-1251");
    public void processHtml(File file) throws Exception {
        // File file = new File(reportDirectory, fileName);
        if (file.exists()) {
            String pri = FileUtils.readFileToString(file, "WINDOWS-1251");
            if (pri.contains("<html lang=")) {
//                Document document = Jsoup.parse(pri, "WINDOWS-1251");
//                document.select("");
                // TransformerFactory factory = TransformerFactory.newInstance();
                // Source xslt = new StreamSource(FileUtilsHelper.getCoreResourceInputStream("report/transform.xslt"));
                // Transformer transformer = factory.newTransformer(xslt);
                //
                // Source text = new StreamSource(file);
                // transformer.transform(text, new StreamResult(new File("output.xml")));

                // // NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
                // // wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
                // // ndp.unmarshalDefaultNumbering();
                // //
                // // wordMLPackage.getMainDocumentPart().getContent()
                // //              .addAll(new XHTMLImporterImpl(wordMLPackage).convert(pri, null));
                // //
                // // System.out.println(XmlUtils.marshaltoString(wordMLPackage
                // //     .getMainDocumentPart().getJaxbElement(), true, true));
                // //
                // // // wordMLPackage.save(new java.io.File(System.getProperty("user.dir") + "/html_output.docx"));
                // // System.out.println("done");
                //
                //
                // // TikaConverter
                // // pri = getWellFormed(pri);
                // // pri = "<html><head><title>Import me</title></head><body><p>Hello World!</p></body></html>";
                // // mdp.getContent().addAll(xhtmlImporter.convert(pri, null));
                //
                // // pri = TikaConverter.processHTML(new StringInputStream(pri, "WINDOWS-1251"));
                //

                XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
                xhtmlImporter.setHyperlinkStyle("Hyperlink");

                AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
                afiPart.setBinaryData(pri.getBytes(W_1));
                afiPart.setContentType(new ContentType("text/html"));
                Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);

                CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
                ac.setId(altChunkRel.getId());
                wordMLPackage.getMainDocumentPart().addObject(ac);

                wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html;charset=UTF_8");
            }
        }
    }

    private void processCsvData(File reportDirectory, String fileName, boolean addBorders, boolean markHeader, Integer fontSize,
                                boolean isWidgets) throws Exception {
        File file = new File(reportDirectory, fileName);
        if (file.exists()) {
            TableStore tableStore = getTableStoreFromFile(file);
            Tbl table = createTableWithContent(tableStore, markHeader, fontSize, isWidgets);

            //setTableCellMargin должен быть перед методом addTableBorders !!
            if (addBorders) {
                setTableCellMargin(table);
                setTableColumnSize(table, tableStore.rows.get(0).size());
                addTableBorders(table);
            }

            if (!isWidgets) {
                setTableWidth(table);
            }

            mdp.addObject(table);

            for (List<String> line : tableStore.longRows) {
                createTableForLongRow(line, fontSize);
            }
        }
    }

    private void processPngData(File reportDirectory) throws Exception {
        File pngDataFile = new File(reportDirectory, "data.png");
        if (pngDataFile.exists()) {
            P p = createImage(getImageBytes(pngDataFile));
            mdp.addObject(p);

            File imageCaptionFile = new File(reportDirectory, "caption.txt");
            if (imageCaptionFile.exists()) {
                imageCounter++;
                String captionText = FileUtils.readFileToString(imageCaptionFile, "UTF-8");
                String captionTitleText = "Рисунок";
                mdp.addObject(createCaption(captionText, captionTitleText));
            }
        }
    }

    private void processTopPngData() throws Exception {
        File leftTopFile = new File(leftTopImagePath);
        File rightTopFile = new File(rightTopImagePath);

        Tc tableCellFirst = createCellForPng(leftTopFile);
        Tc tableCellSecond = createCellForPng(rightTopFile);
        if (tableCellFirst == null && tableCellSecond == null) {
            return;
        }

        tableCellFirst = tableCellFirst == null ? createEmptyCell() : tableCellFirst;
        tableCellSecond = tableCellSecond == null ? createEmptyCell() : tableCellSecond;

        Tr tableRow = wmlObjectFactory.createTr();

        updateTableCellJc(tableCellFirst, JcEnumeration.LEFT);
        tableRow.getContent().add(tableCellFirst);

        updateTableCellJc(tableCellSecond, JcEnumeration.RIGHT);
        tableRow.getContent().add(tableCellSecond);

        Tbl table = wmlObjectFactory.createTbl();
        TblPr tblPr = getTablePr(table);
        setTableColumnSize(table, 2);
        tblPr.setTblW(createWidth(DEFAULT_PAGE_SIZE.toString()));
        table.getContent().add(tableRow);

        mdp.addObject(table);
    }

    private static void updateTableCellJc(Tc cell, JcEnumeration justification) {
        P p = (P) cell.getContent().get(0);
        PPr ppr = getParagraphPr(p);
        ppr.setJc(createJustification(justification));
        PPrBase.Spacing spacing = new PPrBase.Spacing();
        spacing.setAfter(new BigInteger("0"));
        ppr.setSpacing(spacing);
    }

    private byte[] getImageBytes(File imageFile) throws IOException {
        long length = imageFile.length();
        if (length > Integer.MAX_VALUE) {
            logger.error("File too large: " + imageFile.getName());
        }
        byte[] bytes = new byte[(int) length];

        try (InputStream is = new FileInputStream(imageFile)) {
            Integer offset = 0;
            Integer numRead;
            while (offset < bytes.length) {
                numRead = is.read(bytes, offset, bytes.length - offset);
                if (numRead < 0) {
                    break;
                }
                offset += numRead;
            }

            if (offset < bytes.length) {
                logger.error("Could not completely read file " + imageFile.getName());
            }
        }

        return bytes;
    }

    private P createImage(byte[] bytes) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        Inline inline = imagePart.createImageInline(null, null, 0, 1, false);

        Tc tc = createCellWithPicture(inline);
        return (P) tc.getContent().get(0);
    }

    private P createCaption(String captionText, String captionTitleText) {
        P p = wmlObjectFactory.createP();
        addTextValueToCaption(captionTitleText + " ", p, true, false);

        CTSimpleField simpleField = wmlObjectFactory.createCTSimpleField();
        JAXBElement<CTSimpleField> simpleFieldWrapped = wmlObjectFactory.createPFldSimple(simpleField);
        p.getContent().add(simpleFieldWrapped);
        addTextValueToCaption(String.valueOf(imageCounter), p, false, true);
        simpleField.setInstr(" SEQ " + captionTitleText + " \\* ARABIC ");

        addTextValueToCaption(".", p, true, false);
        addTextValueToCaption(" ", p, true, false);
        addTextValueToCaption(captionText, p, true, false);

        PPr ppr = getParagraphPr(p);

        PPrBase.PStyle pprBasePStyle = wmlObjectFactory.createPPrBasePStyle();
        ppr.setPStyle(pprBasePStyle);
        pprBasePStyle.setVal("Caption");

        ppr.setJc(createJustification(JcEnumeration.CENTER));

        return p;
    }

    private static void addTextValueToCaption(String value, P p, boolean addSpace, boolean addRpr) {
        R r = wmlObjectFactory.createR();
        p.getContent().add(r);

        Text text = wmlObjectFactory.createText();
        JAXBElement<Text> textWrapped = wmlObjectFactory.createRT(text);
        r.getContent().add(textWrapped);
        text.setValue(value);
        if (addSpace) {
            text.setSpace("preserve");
        }
        if (addRpr) {
            RPr rpr = wmlObjectFactory.createRPr();
            r.setRPr(rpr);

            BooleanDefaultTrue booleanDefaultTrue = wmlObjectFactory.createBooleanDefaultTrue();
            rpr.setNoProof(booleanDefaultTrue);
        }
    }

    private void createTableForLongRow(List<String> line, Integer fontSize) {
        P header = mdp.createStyledParagraphOfText("Heading3", line.get(0));
        setCollapsed(header);
        mdp.addObject(header);

        Tr row = createRowForString(line.get(1), fontSize);
        Tbl table = wmlObjectFactory.createTbl();
        table.getContent().add(row);

        mdp.addObject(table);
    }

    private Tr createRowForString(String line, Integer fontSize) {
        List<P> paragraphs = createParagraphsWithText(line, fontSize, false);
        Tc tc = wmlObjectFactory.createTc();
        for (P p : paragraphs) {
            setParagraphIndent(p);
            tc.getContent().add(p);
        }

        Tr row = wmlObjectFactory.createTr();
        row.getContent().add(tc);
        return row;
    }

    private void setParagraphIndent(P p) {
        PPr ppr = getParagraphPr(p);
        PPrBase.Spacing spacing = new PPrBase.Spacing();
        spacing.setAfter(new BigInteger(DISTANCE_BETWEEN_TEXT_LINES));
        ppr.setSpacing(spacing);
    }

    private void setCollapsed(P header) {
        if (collapseLongSections) {
            PPr ppr = getParagraphPr(header);
            BooleanDefaultTrue collapsed = new BooleanDefaultTrue();
            ppr.setCollapsed(collapsed);
        }
    }

    private Tbl createTableWithContent(TableStore tableStore, boolean markHeader, Integer fontSize, boolean isWidgets) {
        Tbl table = wmlObjectFactory.createTbl();

        boolean needsToColorNextRow = false;
        for (int i = 0; i < tableStore.rows.size(); i++) {
            boolean isHeader = i == 0 && markHeader;
            Tr tableRow = wmlObjectFactory.createTr();
            for (String cellValue : tableStore.rows.get(i)) {
                addTableCell(tableRow, cellValue, isHeader, fontSize, isWidgets);
            }

            if (isHeader) {
                setRowColor(tableRow, TABLE_TOP_COLOR);
            } else {
                if (isWidgets && needsToColorNextRow) {
                    setRowColor(tableRow, TABLE_ROW_COLOR);
                }
                needsToColorNextRow = !needsToColorNextRow;
            }

            table.getContent().add(tableRow);
        }

        return table;
    }

    private List<List<String>> getRowsFromCsv(File file) throws IOException {
        List<String> csvLines = FileUtils.readLines(file, "UTF-8");
        csvLines = PrintableReportUtils.fixCsvLinesWithNewLineSeparators(csvLines, NEW_LINE_TEMP_STRING);
        List<List<String>> rows = new ArrayList<>();
        for (String line : csvLines) {
            String[] cellValues = line.split("\";\"");

            String firstCell = cellValues[0];
            cellValues[0] = firstCell.replaceFirst("\"", "");

            String lastCell = cellValues[cellValues.length - 1];
            cellValues[cellValues.length - 1] = lastCell.substring(0, lastCell.length() - 1);
            List<String> row = Arrays.asList(cellValues);
            rows.add(row);
        }
        return rows;
    }

    private Tc createCellForPng(File topFile) throws Exception {
        if (!topFile.exists()) {
            return null;
        }
        BinaryPartAbstractImage imagePart1 = BinaryPartAbstractImage.createImagePart(wordMLPackage, getImageBytes(topFile));
        Inline inline = imagePart1.createImageInline(null, null, 0, 1, false);
        return createCellWithPicture(inline);
    }

    private Tc createEmptyCell() {
        return createCellWithPicture(null);
    }

    private Tc createCellWithPicture(Inline inline) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        P p = factory.createP();
        R run = factory.createR();
        p.getContent().add(run);
        if (inline != null) {
            CTPositiveSize2D ctPositiveSize2D = inline.getExtent();
            if (ctPositiveSize2D.getCy() > RESIZE_IMAGE_Y_NUMBER - RESIZE_IMAGE_Y_DELTA
                && ctPositiveSize2D.getCy() < RESIZE_IMAGE_Y_NUMBER + RESIZE_IMAGE_Y_DELTA) {
                ctPositiveSize2D.setCx((long) (ctPositiveSize2D.getCx() * COMPRESS_IMAGE_PERCENTS));
                ctPositiveSize2D.setCy((long) (ctPositiveSize2D.getCy() * COMPRESS_IMAGE_PERCENTS));
            }
            Drawing drawing = factory.createDrawing();
            run.getContent().add(drawing);
            drawing.getAnchorOrInline().add(inline);
        }
        Tc tableCell = wmlObjectFactory.createTc();
        tableCell.getContent().add(p);
        return tableCell;
    }

    private static void addLineToParagraph(P p, String line) {
        R r = wmlObjectFactory.createR();
        Text t = wmlObjectFactory.createText();
        t.setValue(fixLineWithXMLTags(line));
        r.getContent().add(t);
        p.getContent().add(r);
    }

    private void addTableCell(Tr tableRow, String content, boolean isHeader, Integer fontSize, boolean isWidgets) {
        if (checkForHyperlink(content)) {
            Tc tc = getTableCellAsHyperlink(content, fontSize, isWidgets);
            tableRow.getContent().add(tc);
            return;
        }

        List<P> paragraphs = createParagraphsWithText(content, fontSize, isHeader);
        Tc tableCell = wmlObjectFactory.createTc();
        for (P p : paragraphs) {
            if (!isWidgets) {
                setParagraphIndent(p);
            }
            if (isWidgets) {
                centerParagraph(p);
            }
            tableCell.getContent().add(p);
        }
        tableRow.getContent().add(tableCell);
    }

    private List<P> createParagraphsWithText(String content, Integer fontSize, boolean isHeader) {
        String localContent = content.replaceAll(NEW_LINE_TEMP_STRING + NEW_LINE_TEMP_STRING, NEW_LINE_TEMP_STRING);
        String[] values = localContent.split(NEW_LINE_TEMP_STRING);
        List<P> paragraphs = new ArrayList<>();
        for (String value : values) {
            P p = mdp.createParagraphOfText(fixLineWithXMLTags(value));
            RPr rpr = new RPr();
            changeFontSize(rpr, fontSize);
            changeBold(rpr, isHeader);
            R r = (R) p.getContent().get(0);
            r.setRPr(rpr);
            paragraphs.add(p);
        }
        return paragraphs;
    }

    private Tc getTableCellAsHyperlink(String content, Integer fontSize, boolean isWidgets) {
        org.docx4j.relationships.ObjectFactory reFactory = new org.docx4j.relationships.ObjectFactory();
        Relationship rel = reFactory.createRelationship();
        rel.setType(Namespaces.HYPERLINK);
        String actualContent = fixLineWithXMLTags(content);
        rel.setTarget(actualContent);
        rel.setTargetMode("External");
        mdp.getRelationshipsPart().addRelationship(rel);

        P.Hyperlink hyperlink = new P.Hyperlink();
        hyperlink.setId(rel.getId());

        RStyle style = new RStyle();
        style.setVal("Hyperlink");

        RPr rpr = new RPr();
        rpr.setRStyle(style);
        changeFontSize(rpr, fontSize);

        R r = new R();
        r.setRPr(rpr);

        Text t = new Text();
        t.setValue(actualContent);

        r.getContent().add(t);
        hyperlink.getContent().add(r);

        P p = new P();
        if (isWidgets) {
            centerParagraph(p);
        }
        p.getContent().add(hyperlink);

        Tc tableCell = wmlObjectFactory.createTc();
        changeBold(rpr, false);
        tableCell.getContent().add(p);

        return tableCell;
    }

    private void addTableBorders(Tbl table) {
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("2"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);

        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);

        TblPr tblPr = getTablePr(table);
        tblPr.setTblBorders(borders);
    }

    private TableStore getTableStoreFromFile(File file) throws IOException {
        List<List<String>> fileRows = getRowsFromCsv(file);

        TableStore tableStore = getTableStore(fileRows);

        List<List<String>> fixedRows = new ArrayList<>();
        List<List<String>> longRows = new ArrayList<>();
        for (int i = 0; i < fileRows.size(); i++) {
            if (tableStore.longRowsNumbers.contains(i) && fileRows.get(i).size() == 2) {
                longRows.add(fileRows.get(i));
            } else {
                fixedRows.add(fileRows.get(i));
            }
        }

        tableStore.rows = fixedRows;
        tableStore.longRows = longRows;
        return tableStore;
    }

    private TableStore getTableStore(List<List<String>> rows) {
        Set<Integer> longRowsNumbers = new HashSet<>();
        for (int i = 0; i < rows.size(); i++) {
            boolean containsLongText = false;
            for (int j = 0; j < rows.get(i).size(); j++) {
                String text = rows.get(i).get(j);
                if (text.length() > MAX_CELL_TEXT_LENGTH_TO_COLLAPSE) {
                    containsLongText = true;
                }
            }
            if (containsLongText) {
                longRowsNumbers.add(i);
            }
        }

        TableStore tableStore = new TableStore();
        tableStore.longRowsNumbers = longRowsNumbers;
        return tableStore;
    }

    private void setRowColor(Tr tableRow, String color) {
        for (int i = 0; i < tableRow.getContent().size(); i++) {
            CTShd ctShd = Context.getWmlObjectFactory().createCTShd();
            ctShd.setColor(color);
            ctShd.setFill(color);

            Tc tableCell = (Tc) tableRow.getContent().get(i);
            TcPr tcPr = getCellPr(tableCell);
            tcPr.setShd(ctShd);

            tableCell.setTcPr(tcPr);
        }
    }

    private static void setTableWidth(Tbl table) {
        TblPr tblPr = getTablePr(table);
        TblWidth tblWidth = createWidth(DEFAULT_PAGE_SIZE.toString());
        tblPr.setTblW(tblWidth);
    }

    private static void centerParagraph(P p) {
        PPr ppr = getParagraphPr(p);
        ppr.setJc(createJustification(JcEnumeration.CENTER));
        PPrBase.Spacing spacing = new PPrBase.Spacing();
        spacing.setAfter(new BigInteger("0"));
        ppr.setSpacing(spacing);
    }

    private static void setTableCellMargin(Tbl tbl) {
        TblPr tblPr = getTablePr(tbl);
        CTTblCellMar cellMar = new CTTblCellMar();
        tblPr.setTblCellMar(cellMar);
        TblWidth topW = createWidth(CELL_MARGIN_TOP);
        cellMar.setTop(topW);

        TblWidth rightW = createWidth(CELL_MARGIN_RIGHT);
        cellMar.setRight(rightW);

        TblWidth btW = createWidth(CELL_MARGIN_BOTTOM);
        cellMar.setBottom(btW);

        TblWidth leftW = createWidth(CELL_MARGIN_LEFT);
        cellMar.setLeft(leftW);
    }

    private static void setTableColumnSize(Tbl tbl, Integer columnsCount) {
        TblGrid grid = getTableGrid(tbl);

        List<TblGridCol> cols = grid.getGridCol();
        Integer eachColumnWidth = DEFAULT_PAGE_SIZE / columnsCount;
        for (int i = 0; i < columnsCount; i++) {
            cols.add(createGrid(eachColumnWidth.toString()));
        }

        TblPr tblPr = getTablePr(tbl);
        tblPr.setJc(createJustification(JcEnumeration.CENTER));

        CTTblLayoutType layoutType = new CTTblLayoutType();
        layoutType.setType(STTblLayoutType.FIXED);
        tblPr.setTblLayout(layoutType);
    }

    private static TblGrid getTableGrid(Tbl tbl) {
        TblGrid grid = tbl.getTblGrid();
        if (grid == null) {
            grid = new TblGrid();
            tbl.setTblGrid(grid);
        }
        return grid;
    }

    private static PPr getParagraphPr(P p) {
        PPr ppr = p.getPPr();
        if (ppr == null) {
            ppr = new PPr();
            p.setPPr(ppr);
        }
        return ppr;
    }

    private static TblPr getTablePr(Tbl tbl) {
        TblPr tblPr = tbl.getTblPr();
        if (tblPr == null) {
            tblPr = new TblPr();
            tbl.setTblPr(tblPr);
        }
        return tblPr;
    }

    private static TcPr getCellPr(Tc tableCell) {
        TcPr tcPr = tableCell.getTcPr();
        if (tcPr == null) {
            tcPr = new TcPr();
            tableCell.setTcPr(tcPr);
        }
        return tcPr;
    }

    private static TblGridCol createGrid(String width) {
        TblGridCol col = new TblGridCol();
        col.setW(new BigInteger(width));
        return col;
    }

    private static Jc createJustification(JcEnumeration justification) {
        Jc jc = new Jc();
        jc.setVal(justification);
        return jc;
    }

    private static TblWidth createWidth(String width) {
        TblWidth tblWidth = new TblWidth();
        tblWidth.setW(new BigInteger(width));
        tblWidth.setType(DEFAULT_TABLE_WIDTH_UNITS);
        return tblWidth;
    }

    private static boolean checkForHyperlink(String content) {
        Pattern pattern = Pattern.compile("^[ \t\n\r]*([hH][tT][tT][pP]|[wW][wW][wW])");
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    private static String fixLineWithXMLTags(String line) {
        if (line.contains("<a href=\"#project")) {
            return "";
        }
        return line;
    }

    private class TableStore {
        Set<Integer> longRowsNumbers = new HashSet<>();
        List<List<String>> rows = new ArrayList<>();
        List<List<String>> longRows = new ArrayList<>();
    }
}

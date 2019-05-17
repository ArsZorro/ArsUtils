import org.junit.Test;

import report.Docx4jService;

public class Docx4jTest {

    @Test
    public void convertWordFileToXml() {
        Docx4jService docx4jService = new Docx4jService();
        docx4jService.convertFile("C:\\Users\\User\\Desktop\\tests", "Цвет Строк (ООО Тополек) Либре .docx");
    }

    @Test
    public void convertXlsxFileToXml() {
        Docx4jService docx4jService = new Docx4jService();
        docx4jService.convertXlsxFile("C:\\Users\\User\\Desktop\\tests", "АНКЕТА ПАО РТК_Гончарова.xlsx");
    }

    @Test
    public void testCount() {
        Integer columnWidth = 1000 / 3;
        System.out.println(columnWidth);
    }
}

import org.junit.Test;

import report.Docx4jService;

public class Docx4jTest {

    @Test
    public void convertFileToXml() {
        Docx4jService docx4jService = new Docx4jService();
        docx4jService.convertFile("C:\\Users\\User\\Desktop\\tests", "Цвет Строк (ООО Тополек) Либре .docx");
    }

    @Test
    public void testCount() {
        Integer columnWidth = 1000 / 3;
        System.out.println(columnWidth);
    }
}

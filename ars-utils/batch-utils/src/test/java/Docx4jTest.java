import org.junit.Test;

import report.Docx4jService;

public class Docx4jTest {

    @Test
    public void convertFileToXml() {
        Docx4jService docx4jService = new Docx4jService();
        docx4jService.convertFile("C:\\Users\\User\\Desktop\\tests", "Отступ.docx");
    }
}

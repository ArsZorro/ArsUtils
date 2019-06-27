package report.support;

import java.io.*;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.xlsx4j.sml.CTStylesheet;

public class PurchaseReportUtils {
    private static volatile PurchaseReportUtils instance;
    private Map<Integer, String> position2Symbol;
    private CTStylesheet ctStylesheet;
    private List<String> defaultCheckListMergeCells;
    private List<String> defaultPassportMergeCells;

    private PurchaseReportUtils() {
        this.position2Symbol = new HashMap<>();

        try {
            List<String> symbols = Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z");
            Integer counter = 1;
            for (int i = 0; i < symbols.size(); i++) {
                position2Symbol.put(counter, symbols.get(i));
                counter++;
            }
            for (int i = 0; i < symbols.size(); i++) {
                for (int j = 0; j < symbols.size(); j++) {
                    position2Symbol.put(counter, symbols.get(i) + symbols.get(j));
                    counter++;
                }
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(CTStylesheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Resource resource = new DefaultResourceLoader().getResource("classpath:" + "report" + File.separator + "styles_unmarshal.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF8"));

            JAXBElement o = (JAXBElement) jaxbUnmarshaller.unmarshal(in);

            this.ctStylesheet = (CTStylesheet) o.getValue();

            this.defaultCheckListMergeCells = Arrays.asList("B2:C2", "A2:A3", "A9:A10", "B1:C1", "B9:B10");
            this.defaultPassportMergeCells = Arrays.asList("E10:E11", "B1:P2", "A10:A11", "B10:B11", "C10:C11",
                "D10:D11", "F10:F11", "A1:A2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PurchaseReportUtils getInstance() {
        PurchaseReportUtils localInstance = instance;
        if (localInstance == null) {
            synchronized (PurchaseReportUtils.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = new PurchaseReportUtils();
                    instance = localInstance;
                }
            }
        }
        return localInstance;
    }

    public CTStylesheet getCtStylesheet() {
        return ctStylesheet;
    }

    public Map<Integer, String> getPosition2Symbol() {
        return position2Symbol;
    }

    public List<String> getDefaultCheckListMergeCells() {
        return defaultCheckListMergeCells;
    }

    public List<String> getDefaultPassportMergeCells() {
        return defaultPassportMergeCells;
    }
}

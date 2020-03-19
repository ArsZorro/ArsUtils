import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.CharUtils;
import org.junit.Test;

public class StringTests {
    String constants = "Advert\n"
                       + "Album\n"
                       + "ArbitrCourtCase\n"
                       + "AutoruAdvert\n"
                       + "AvitoAdvert\n"
                       + "BankruptOrganization\n"
                       + "BankruptPerson\n"
                       + "BlockedWebResource\n"
                       + "Comment\n"
                       + "Contact\n"
                       + "FbAlbum\n"
                       + "FbComment\n"
                       + "FbGroup\n"
                       + "FbPerson\n"
                       + "FbPhoto\n"
                       + "FbPost\n"
                       + "FleetmonVessel\n"
                       + "Forecast\n"
                       + "GibddAccident\n"
                       + "GibddDriver\n"
                       + "GibddFine\n"
                       + "GibddVehicle\n"
                       + "GidrometForecast\n"
                       + "GoogleSearchResult\n"
                       + "Group\n"
                       + "HhOrganization\n"
                       + "HhResume\n"
                       + "HhVacancy\n"
                       + "LjAlbum\n"
                       + "LjComment\n"
                       + "LjGroup\n"
                       + "LjPerson\n"
                       + "LjPhoto\n"
                       + "LjPost\n"
                       + "MarineTrafficVessel\n"
                       + "OgrnOrganization\n"
                       + "OgrnPerson\n"
                       + "OkAlbum\n"
                       + "OkComment\n"
                       + "OkGroup\n"
                       + "OkPerson\n"
                       + "OkPhoto\n"
                       + "OkPost\n"
                       + "Organization\n"
                       + "OrganizationFake\n"
                       + "Person\n"
                       + "Photo\n"
                       + "PhotoTag\n"
                       + "Post\n"
                       + "Relative\n"
                       + "RssWebResource\n"
                       + "SearchEngineResult\n"
                       + "TwitterAlbum\n"
                       + "TwitterGroup\n"
                       + "TwitterPerson\n"
                       + "TwitterPhoto\n"
                       + "TwitterPost\n"
                       + "Vessel\n"
                       + "VkAlbum\n"
                       + "VkComment\n"
                       + "VkGroup\n"
                       + "VkPerson\n"
                       + "VkPhoto\n"
                       + "VkPost\n"
                       + "WebPageWebResource\n"
                       + "WebResource\n"
                       + "YandexForecast\n"
                       + "YandexSearchResult\n"
                       + "ZakupkiContract\n"
                       + "ZakupkiOrganization\n"
                       + "ZakupkiPlan\n"
                       + "ZakupkiPurchase";

    @Test
    public void convertNamesToConstantNames() {
        String[] lines = constants.split("\n");

        for (String line : lines) {
            List<String> words = detectWordsByCase(line);
            System.out.println(words.stream().map(String::toUpperCase).collect(Collectors.joining("_")));
        }
    }

    private List<String> detectWordsByCase(String line) {
        List<String> words = new ArrayList<>();
        char[] chars = line.toCharArray();
        StringBuilder lastWord = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i != 0 && Character.isUpperCase(chars[i])) {
                words.add(lastWord.toString());
                lastWord = new StringBuilder();
            }
            lastWord.append(chars[i]);
        }
        words.add(lastWord.toString());
        return words;
    }
}

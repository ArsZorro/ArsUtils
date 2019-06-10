import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class BillingLocationCutter {
    @Test
    public void test() throws Exception {
        List<String> list = read("triumph_egor.csv");
        StringBuilder fixed = new StringBuilder();

        int counter = 0;
        for (String line : list) {
            if (counter % 10000 == 0) {
                System.out.println(counter);
            }
            fixed.append(fixLine(line)).append("\n");
            counter++;
        }

        write(fixed, "triumph_egor_fixed.csv");
        System.out.println();
    }

    @Test
    public void test244() throws Exception {
        // List<String> lines = read("bs_from_20.csv");
        //
        // StringBuilder stringBuilder = new StringBuilder();
        // for (String line : lines) {
        //     String[] ids = getBSids(line);
        //     if (ids != null) {
        //         stringBuilder.append(ids[0]).append(";").append(ids[1]).append("\n");
        //     }
        // }

        // String prefix = System.currentTimeMillis() + "";
        String prefix = "7_";
        // write(stringBuilder, prefix + "bs_ids_in_needed_squere.csv");

        filterNeededSessions(prefix, "bs_ids_in_needed_squere.csv");
    }

    private List<String> read(String fileName) throws Exception {
        return IOUtils.readLines(getClass().getClassLoader().getResourceAsStream(fileName));
    }

    private void write(StringBuilder stringBuilder, String name) {
        try {
            FileWriter fileWriter = new FileWriter(new File(name));
            fileWriter.append(stringBuilder);
            fileWriter.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String[] getBSids(String line) {
        String[] elements = line.split(";");
        String str_lat = elements[elements.length - 2];
        String str_lon = elements[elements.length - 1];
        try {
            Double lon = Double.parseDouble(str_lat);
            Double lat = Double.parseDouble(str_lon);
            if (lat >= 30.297245 && lat <=  30.365194 && lon >= 59.945183 && lon <= 59.927559) {
                String[] ids = new String[2];
                ids[0] = elements[3];
                ids[1] = elements[4];
                return ids;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private String fixLine(String line) {
        Pattern pattern = Pattern.compile("\\d+[;]\\d+[;]\\d+[;]\\d+");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String value = matcher.group();
            String one = line.substring(0, start);
            String third = line.substring(end, line.length());

            StringBuilder stringBuilder = new StringBuilder();
            String[] fixed = value.split(";");
            for (int i = 0; i < fixed.length; i++) {
                stringBuilder.append(fixed[i]);
                if (i + 1 < fixed.length) {
                    stringBuilder.append("\";\"");
                }
            }
            line = one + stringBuilder.toString() + third;
            System.out.println();
        }
        return line;
    }

    public void filterNeededSessions(String prefix, String postfix) throws Exception {
        Set<String> ids = parseIds(read(prefix + postfix));

        List<String> sessions = read("1_triumph_egor_fixed_spb_center.csv");
        StringBuilder stringBuilder = new StringBuilder();
        for (String session : sessions) {
            String[] sessionSplited = session.split(";");
            if (isSessionSuit(sessionSplited, ids)) {
                stringBuilder.append(session).append("\n");
            }
        }

        write(stringBuilder, prefix + "triumph_egor_fixed_spb_center.csv");
        System.out.println();
    }

    private boolean isSessionSuit(String[] sessionSplited, Set<String> ids) {
        String curentId = sessionSplited[21].replace("\"", "") + "-" + sessionSplited[22].replace("\"", "");
        return ids.contains(curentId);
    }

    private Set<String> parseIds(List<String> ids_lines) {
        Set<String> ids = new HashSet<>();
        for (String line : ids_lines) {
            String[] local_ids = line.split(";");
            if (local_ids.length >= 2) {
                String firstId = local_ids[0].replace("\"", "");
                String secondId = local_ids[1].replace("\"", "");

                ids.add(firstId + "-" + secondId);
            }
        }
        return ids;
    }
}

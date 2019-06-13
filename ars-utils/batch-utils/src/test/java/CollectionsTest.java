import java.util.*;

import org.junit.Test;

import collections.CollectionsUtils;

public class CollectionsTest {

    @Test
    public void testSubColections() {
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5");
        List<List<String>> subs = CollectionsUtils.getSubCollections(strings, 2);
        System.out.println();
    }
}

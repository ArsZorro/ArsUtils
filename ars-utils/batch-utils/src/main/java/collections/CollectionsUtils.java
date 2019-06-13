package collections;

import java.util.*;

public class CollectionsUtils {

    public static <E extends Object> List<List<E>> getSubCollections(Collection<E> input, int subCollectionSize) {
        List<List<E>> result = new ArrayList<>();
        int subCounter = 0;
        List<E> subCollection = new ArrayList<>();
        for (E e : input) {
            if (subCounter < subCollectionSize) {
                subCollection.add(e);
                subCounter++;
            } else {
                result.add(subCollection);
                subCollection = new ArrayList<>();
                subCollection.add(e);
                subCounter = 1;
            }
        }
        if (subCollection.size() > 0) {
            result.add(subCollection);
        }
        return result;
    }

}

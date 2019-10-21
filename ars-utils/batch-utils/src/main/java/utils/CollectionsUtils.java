package utils;

import java.util.*;
import java.util.function.BiConsumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class CollectionsUtils {
    protected CollectionsUtils() {}

    public static <K, V> List<V> getAllListValues(Map<K, List<V>> map) {
        List<V> allValues = new ArrayList<>();
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            allValues.addAll(entry.getValue());
        }
        return allValues;
    }

    public static <T> T coalesce(T... values) {
        for (T value : values) {
            if (value instanceof Optional) {
                if (((Optional) value).isPresent()) {
                    return value;
                }
            } else {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    public static String coalesceNotBlank(String... values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    public static <K, V> String getValueAsStringOrNullIfBlank(Map<K, V> data, K key) {
        String value = getValueAsStringOrNull(data, key);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return null;
    }

    public static <K, V> String getValueAsStringOrNull(Map<K, V> data, K key) {
        String resultString = null;
        V resultObject = data.getOrDefault(key, null);
        if (resultObject != null) {
            if (resultObject instanceof String) {
                resultString = (String) resultObject;
            }

            if (resultObject instanceof Number) {
                resultString = resultObject.toString();
            }
        }
        return resultString;
    }

    public static <K, V> List<V> getValuesByKeys(Map<K, V> map, Set<K> keys) {
        List<V> values = new ArrayList<>();
        for (K key : keys) {
            V value = map.get(key);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    //Идея в том, чтобы пройтись по каждому листу только один раз,
    //оба листа должны быть отсортированы по одинаковому критерию
    public static <F, S> void processComparableElementsInSortedLists(List<F> firstList,
                                                                     List<S> secondList,
                                                                     BiConsumer<F, S> processor,
                                                                     ListElementsComparator<F, S> comparator) {
        if (CollectionUtils.isEmpty(firstList) || CollectionUtils.isEmpty(secondList)) {
            return;
        }

        int firstMainCounter = 0;
        int secondMainCounter = 0;

        for (; firstMainCounter < firstList.size();) {
            F firstElement = firstList.get(firstMainCounter);

            for (int currentCounter = secondMainCounter; currentCounter < secondList.size(); currentCounter++) {
                S secondElement = secondList.get(currentCounter);

                int compareResult = comparator.compare(firstElement, secondElement);

                if (isFirstElementLess(compareResult)) {
                    break;
                }

                if (comparedElementsAreProcessable(compareResult)) {
                    processor.accept(firstElement, secondElement);
                }

                if (isFirstElementMore(compareResult)) {
                    secondMainCounter++;
                }
            }

            firstMainCounter++;
        }
    }

    private static boolean isFirstElementLess(int compareResult) {
        return compareResult < 0;
    }

    private static boolean comparedElementsAreProcessable(int compareResult) {
        return compareResult == 0;
    }

    private static boolean isFirstElementMore(int compareResult) {
        return compareResult > 0;
    }

    public interface ListElementsComparator<F, S> {
        int compare(F firstElement, S secondElement);
    }
}

package collections;

import org.apache.commons.collections4.CollectionUtils;

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

//    public static void walkSortedListsWithSettingParamsToValidPair(List<Token> tokens,
//                                                                   List<ThemeSentence> themeSentences) {
//        walkSortedListsWithSettingParamsToValidPair(
//                tokens,
//                themeSentences,
//                new PairValidator<Token, ThemeSentence>() {
//                    @Override
//                    public int isPairValid(Token token, ThemeSentence themeSentence) {
//                        if (token.end < themeSentence.start) {
//                            return -1;
//                        }
//
//                        if (token.start >= themeSentence.start && token.start <= themeSentence.end) {
//                            return 0;
//                        }
//
//                        return 1;
//                    }
//                },
//                new ValuesParamsSetter<Token, ThemeSentence>() {
//                    @Override
//                    public void setValuesParams(Token token, ThemeSentence themeSentence) {
//                        token.theme = themeSentence.theme;
//                    }
//                });
//    }

    //Идея в том, чтобы пройтись по каждому листу только один раз,
    //оба листа должны быть отсортированы по одинаковому критерию
    public static <T1, T2> void processComparableElementsInSortedLists(List<T1> firstList,
                                                                       List<T2> secondList,
                                                                       ListElementsProcessor<T1, T2> processor,
                                                                       ListElementsComparator<T1, T2> comparator) {
        if (CollectionUtils.isEmpty(firstList) || CollectionUtils.isEmpty(secondList)) {
            return;
        }

        int firstMainCounter = 0;
        int secondMainCounter = 0;

        for (; firstMainCounter < firstList.size();) {
            T1 firstElement = firstList.get(firstMainCounter);

            for (int j2 = secondMainCounter; j2 < secondList.size(); j2++) {
                T2 secondElement = secondList.get(j2);

                int compareResult = comparator.compare(firstElement, secondElement);

                if (isFirstElementLess(compareResult)) {
                    break;
                }

                if (comparedElementsAreProcessable(compareResult)) {
                    processor.process(firstElement, secondElement);
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

    private static boolean isFinalIteration(int counter, List list) {
        return counter == list.size() - 1;
    }

    public interface ListElementsProcessor<T1, T2>  {
        void process(T1 firstElement, T2 secondElement);
    }

    public interface ListElementsComparator<T1, T2>  {
        int compare(T1 firstElement, T2 secondElement);
    }

    //    public static <T1, T2> void walkSortedListsWithSettingParamsToValidPair(List<T1> list1,
//                                                                            List<T2> list2,
//                                                                            PairValidator<T1, T2> pairValidator,
//                                                                            ValuesParamsSetter<T1, T2> paramsSetter) {
//        if (CollectionUtils.isEmpty(list1) || CollectionUtils.isEmpty(list2)) {
//            return;
//        }
//
//        int counter1 = 0;
//        int counter2 = 0;
//        for (; counter1 < list1.size(); counter1++) {
//            T1 firstLevelT1 = list1.get(counter1);
//            T2 firstLevelT2 = list2.get(counter2);
//            if (pairValidator.isPairValid(firstLevelT1, firstLevelT2) < 0) {
//                setParamsOfAllValid(firstLevelT1, list2, counter2);
//            }
//        }
//    }

    //
//    public static <T1, T2> void walkSortedListsWithSettingParamsToValidPair(List<T1> list1,
//                                                                            List<T2> list2,
//                                                                            PairValidator<T1, T2> pairValidator,
//                                                                            ValuesParamsSetter<T1, T2> paramsSetter) {
//        if (CollectionUtils.isEmpty(list1) || CollectionUtils.isEmpty(list2)) {
//            return;
//        }
//
//        int counter1 = 0;
//        int counter2 = 0;
//        for (; counter1 < list1.size(); counter1++) {
//            T1 firstLevelT1 = list1.get(counter1);
//            T2 firstLevelT2 = list2.get(counter2);
//            if (pairValidator.isPairValid(firstLevelT1, firstLevelT2)) {
//                setParamsOfAllValid(firstLevelT1, list2, counter2);
//            }
//        }
//    }

    private static <T1, T2> void setParamsOfAllValid(T1 firstLevelT1, List<T2> list2, int counter2) {
        int secondLevelCounter2 = counter2;
        for (;secondLevelCounter2 < list2.size(); secondLevelCounter2++) {

        }
    }

    public static abstract class PairValidator <T1, T2> {
        public abstract int isPairValid(T1 value1, T2 value2);
    }

    public static abstract class ValuesParamsSetter <T1, T2> {
        public abstract void setValuesParams(T1 value1, T2 value2);
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
}

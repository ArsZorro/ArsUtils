import java.util.*;

import collections.support.PairValidator;
import collections.support.ValuesParamsSetter;
import entities.ThemeSentence;
import entities.Token;
import org.junit.Test;

import collections.CollectionsUtils;

public class CollectionsTest {

    @Test
    public void testSubColections() {
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5");
        List<List<String>> subs = CollectionsUtils.getSubCollections(strings, 2);
        System.out.println();
    }

    @Test
    public void testWalkCollections() {
        List<Token> tokens = Arrays.asList(
                buildToken(2, 5),
                buildToken(7, 8),
                buildToken(10, 11));

        List<ThemeSentence> themeSentences = Arrays.asList(
                new ThemeSentence(1, 6, "Theme1"),
                new ThemeSentence(7, 12, "Theme2"));

        CollectionsUtils.processComparableElementsInSortedLists(
                tokens,
                themeSentences,
                (t1, s2) -> t1.theme = s2.theme,
                (token, sentence) -> {
                    if (token.end < sentence.start) {
                        return -1;
                    }
                    if (token.end >= sentence.start && token.start <= sentence.end) {
                        return  0;
                    }
                    if (token.start > sentence.end) {
                        return 1;
                    }

                    return -1;
                });
        System.out.println();
//        CollectionsUtils.walkSortedListsWithSettingParamsToValidPair(
//                tokens,
//                themeSentences,
//                new PairValidator<Token, ThemeSentence>() {
//                    @Override
//                    public boolean isPairValid(Token token, ThemeSentence themeSentence) {
//                        return token.start <= themeSentence.end && token.end >= themeSentence.start;
//                    }
//                },
//                new ValuesParamsSetter<Token, ThemeSentence>() {
//                    @Override
//                    public void setValuesParams(Token token, ThemeSentence themeSentence) {
//                        token.theme = themeSentence.theme;
//                    }
//                });
        System.out.println();
    }

    private Token buildToken(int start, int end) {
        Token token = new Token();
        token.start = start;
        token.end = end;
        return token;
    }

    //todo переименовать в tokensprovider getGraphTokenProperties

    @Test
    public void testWalkCollectionsAllTheSamePlace() {
        List<Token> tokens = Arrays.asList(
                buildToken(2, 2),
                buildToken(2, 2),
                buildToken(2, 2));

        List<ThemeSentence> themeSentences = Arrays.asList(
                new ThemeSentence(2, 2, "Theme1"),
                new ThemeSentence(2, 2, "Theme2"));

        CollectionsUtils.processComparableElementsInSortedLists(
                tokens,
                themeSentences,
                (t1, s2) -> t1.theme = s2.theme,
                (token, sentence) -> {
                    if (token.end < sentence.start) {
                        return -1;
                    }
                    if (token.end >= sentence.start && token.start <= sentence.end) {
                        return 0;
                    }
                    if (token.start > sentence.end) {
                        return 1;
                    }

                    return -1;
                });
        System.out.println();
    }


    @Test
    public void testWalkCollectionsAllNotProcessed() {
        List<Token> tokens = Arrays.asList(
                buildToken(0, 0),
                buildToken(0, 0),
                buildToken(0, 0));

        List<ThemeSentence> themeSentences = Arrays.asList(
                new ThemeSentence(2, 2, "Theme1"),
                new ThemeSentence(2, 2, "Theme2"));

        CollectionsUtils.processComparableElementsInSortedLists(
                tokens,
                themeSentences,
                (t1, s2) -> t1.theme = s2.theme,
                (token, sentence) -> {
                    if (token.end < sentence.start) {
                        return -1;
                    }
                    if (token.end >= sentence.start && token.start <= sentence.end) {
                        return 0;
                    }
                    if (token.start > sentence.end) {
                        return 1;
                    }

                    return -1;
                });
        System.out.println();
    }
}

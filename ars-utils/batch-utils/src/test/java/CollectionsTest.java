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
}

package strategy.steps.selectors.filters;

import java.util.*;

import strategy.TextExtractionStore;
import strategy.steps.selectors.TokenSelector;
import strategy.steps.selectors.TokenSelectorResult;

public abstract class TokenSelectionFilter {
    public List<TokenSelector> selectors = new ArrayList<>();

    public TokenSelectorResult filter(TextExtractionStore input, Map<String, TokenSelectorResult> selectionHistory) {
        TokenSelectorResult result = new TokenSelectorResult();
        for (TokenSelector selector : selectors) {
            if (selectionHistory.containsKey(selector.name)) {
                result.addResult(selectionHistory.get(selector.name));
            } else {
                TokenSelectorResult selectionResult = selector.select(input);
                selectionHistory.put(selector.name, selectionResult);
                result.addResult(selectionResult);
            }
        }
        return result;
    }
}

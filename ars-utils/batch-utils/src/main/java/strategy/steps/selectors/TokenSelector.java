package strategy.steps.selectors;

import strategy.TextExtractionStore;
import strategy.steps.selectors.result.SelectorResult;

public abstract class TokenSelector {
    public abstract SelectorResult select(TextExtractionStore input);
}

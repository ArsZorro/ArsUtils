package strategy.steps;

import java.util.function.Consumer;

import entities.Token;
import strategy.TextExtractionStore;
import strategy.steps.selectors.TokenSelector;
import strategy.steps.selectors.result.SelectorResult;

public abstract class SelectorStep implements ExtractionStep {
    public Consumer<Token> whatToDoWithToken;
    public TokenSelector selector;

    public SelectorStep(Consumer<Token> whatToDoWithToken, TokenSelector selector) {
        this.whatToDoWithToken = whatToDoWithToken;
        this.selector = selector;
    }

    @Override
    public void executeStep(TextExtractionStore store) {
        SelectorResult select = selector.select(store);
        select.allTokens.forEach(whatToDoWithToken);
    }
}

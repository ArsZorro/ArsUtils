package strategy.steps;

import java.util.*;

import strategy.TextExtractionStore;
import strategy.steps.executors.TextExtractionStepExecutor;
import strategy.steps.selectors.TokenSelector;

public abstract class TokenSelectorStep implements ExtractionStep {
    public TextExtractionStepExecutor executor;
    public List<TokenSelector> selectors;

    protected TokenSelectorStep(TextExtractionStepExecutor executor, TokenSelector... selectors) {
        this.selectors = Arrays.asList(selectors);
        this.executor = executor;
    }

    public TextExtractionStore executeSelectorStep(TextExtractionStore store) {

    }

    public List<TextExtractionStore> getSelectorsResults(TextExtractionStore store) {
        List<TextExtractionStore> stores = new ArrayList<>();
        List<TokenSelector> currentSelectors = new ArrayList<>();
        for (TokenSelector selector : selectors) {

        }
    }
}

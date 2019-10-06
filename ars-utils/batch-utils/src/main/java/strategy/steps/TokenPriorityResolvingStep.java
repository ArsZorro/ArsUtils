package strategy.steps;

import strategy.TextExtractionStore;

public class TokenPriorityResolvingStep implements ExtractionStep {
    @Override
    public TextExtractionStore executeStep(TextExtractionStore store) {
        // TokensPriorityResolver.resolvePriority(store.graph, store.getAllExtractors());
        return store;
    }
}

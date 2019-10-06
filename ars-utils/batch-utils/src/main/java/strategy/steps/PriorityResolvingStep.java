package strategy.steps;

import strategy.TextExtractionStore;

public class PriorityResolvingStep implements ExtractionStep {
    @Override
    public TextExtractionStore executeStep(TextExtractionStore store) {
        // TokensPriorityResolver.resolvePriority(store.graph, store.getAllExtractors());
        return store;
    }
}

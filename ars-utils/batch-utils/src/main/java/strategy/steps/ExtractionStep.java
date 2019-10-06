package strategy.steps;

import strategy.TextExtractionStore;

public interface ExtractionStep {
    void executeStep(TextExtractionStore store);
}

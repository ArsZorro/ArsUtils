package strategy.steps;

import strategy.TextExtractionStore;

public interface ExtractionStep {
    TextExtractionStore executeStep(TextExtractionStore store);
}

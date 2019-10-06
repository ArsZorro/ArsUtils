package strategy;

import strategy.steps.ExtractionStep;

public class TextExtractionStrategyBuilder {
    private TextExtractionStrategy strategy;

    public TextExtractionStrategyBuilder() {
        this.strategy = new TextExtractionStrategy();
    }

    public TextExtractionStrategyBuilder addStep(ExtractionStep step) {
        this.strategy.steps.add(step);
        return this;
    }

    public TextExtractionStrategy build() {
        return strategy;
    }
}

package strategy.steps;

import entities.Token;
import strategy.TextExtractionStore;
import strategy.steps.selectors.TokenSelector;

import java.util.function.BiConsumer;

public class AdjacentsStep implements ExtractionStep {
    BiConsumer<Token, Token> whatToDoWithAdjacents;
    TokenSelector firstSelector;
    TokenSelector secondSelector;
    Integer minDistance;

    public AdjacentsStep(BiConsumer<Token, Token> whatToDoWithAdjacent, TokenSelector firstSelector, TokenSelector secondSelector, Integer minDistance) {
        this.whatToDoWithAdjacents = whatToDoWithAdjacent;
        this.firstSelector = firstSelector;
        this.secondSelector = secondSelector;
        this.minDistance = minDistance;
    }

    @Override
    public void executeStep(TextExtractionStore store) {

    }
}

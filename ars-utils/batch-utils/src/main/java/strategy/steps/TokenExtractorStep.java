package strategy.steps;

import strategy.TextExtractionStore;
import strategy.TokenExtractor;
import strategy.TokenExtractorResult;

public class TokenExtractorStep implements ExtractionStep {
    TokenExtractor tokenExtractor;

    public TokenExtractorStep(TokenExtractor tokenExtractor) {
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public TextExtractionStore executeStep(TextExtractionStore store) {
        TokenExtractorResult result = this.tokenExtractor.extract(store.inputText);
        store.addExtractionResult(result);
        return store;
    }
}

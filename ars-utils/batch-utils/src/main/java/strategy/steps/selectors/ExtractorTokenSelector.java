package strategy.steps.selectors;

import strategy.TextExtractionStore;
import strategy.TokenExtractorType;
import strategy.steps.selectors.result.SelectorResult;

public class ExtractorTokenSelector extends TokenSelector {
    public String extractorName;
    public TokenExtractorType extractorType;

    public ExtractorTokenSelector(String extractorName) {
        this.extractorName = extractorName;
    }

    public ExtractorTokenSelector(TokenExtractorType extractorType) {
        this.extractorType = extractorType;
    }

    @Override
    public SelectorResult select(TextExtractionStore input) {
        SelectorResult result = new SelectorResult();
        if (extractorName != null) {
            result.addResult(input.extractorName2Result.get(extractorName));
        }
        if (extractorType != null) {
            result.addResult(input.extractorType2Result.get(extractorType));
        }
        return result;
    }
}

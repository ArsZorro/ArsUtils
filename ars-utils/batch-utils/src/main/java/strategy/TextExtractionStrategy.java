package strategy;

import java.util.*;

import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;

import factograph.text.strategy.steps.ExtractionStep;
import factograph.text.strategy.steps.TokenSelectorStep;
import factograph.text.strategy.steps.selectors.TokenSelector;
import factograph.text.strategy.steps.selectors.TokenSelectorResult;
import factograph.text.strategy.steps.selectors.filters.TokenSelectionFilter;
import strategy.steps.ExtractionStep;
import strategy.steps.TokenSelectorStep;
import strategy.steps.selectors.TokenSelector;
import strategy.steps.selectors.TokenSelectorResult;
import strategy.steps.selectors.filters.TokenSelectionFilter;

public class TextExtractionStrategy {
    public List<ExtractionStep> steps;
    private Map<String, TokenSelectorResult> selectionHistory = new HashMap<>();

    public TextExtractionStrategy() {
        this(new ArrayList<>());
    }

    public TextExtractionStrategy(List<ExtractionStep> steps) {
        this.steps = steps;
    }

    public TextExtractionStore execute(TextExtractionStore store) {
        for (ExtractionStep extractionStep : steps) {
            if (extractionStep instanceof TokenSelectorStep) {
                TokenSelectorStep selectorStep = (TokenSelectorStep) extractionStep;

                TokenSelectorResult selectionResult = new TokenSelectorResult();
                for (TokenSelector selector : selectorStep.selectors) {
                    Preconditions.checkArgument(CollectionUtils.isNotEmpty(selector.filters) || selectionHistory.containsKey(selector.name),
                        "In selection history was no selection with name:" + selector.name);
                    for (TokenSelectionFilter filter : selector.filters) {
                        selectionResult.addResult(filter.filter(store, selectionHistory));
                    }
                }

                TokenSelectorResult selectionResult = null;
                for (TokenSelector selector : currentSelectors) {
                    if (selectionResult == null) {
                        selectionResult = selector.select(store);
                    } else {
                        selectionResult = selector.select(selectionResult);
                    }
                    selectionHistory.put(selector.name, selectionResult);
                }

                selectorStep.executor.
            }
            extractionStep.executeStep(store);
        }
    }

    public interface SelectWithHistory {
        TokenSelectorResult withHistory(TokenSelector selector);
    }
}

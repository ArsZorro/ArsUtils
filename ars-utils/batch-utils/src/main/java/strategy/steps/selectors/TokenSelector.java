package strategy.steps.selectors;

import java.util.*;

import strategy.TextExtractionStore;
import strategy.steps.selectors.filters.TokenSelectionFilter;

public abstract class TokenSelector {
    public String name;
    public List<TokenSelectionFilter> filters;

    public TokenSelector(String name, TokenSelectionFilter... filters) {
        this.name = name;
        this.filters = Arrays.asList(filters);
    }

    public abstract TokenSelectorResult select(TextExtractionStore input);
}

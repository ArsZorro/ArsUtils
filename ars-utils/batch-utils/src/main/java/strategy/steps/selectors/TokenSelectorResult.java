package strategy.steps.selectors;

import java.util.*;

import factograph.text.model.Token;

public class TokenSelectorResult {
    Set<Token> allTokens = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TokenSelectorResult that = (TokenSelectorResult) o;
        return Objects.equals(allTokens, that.allTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allTokens);
    }

    public void addResult(TokenSelectorResult currentResult) {
        this.allTokens.addAll(currentResult.allTokens);
    }
}

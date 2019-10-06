package strategy.steps.selectors.result;

import java.util.*;

import entities.Token;
import strategy.TokenExtractorResult;

public class SelectorResult {
    public Set<Token> allTokens = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SelectorResult that = (SelectorResult) o;
        return Objects.equals(allTokens, that.allTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allTokens);
    }

    public void addResult(SelectorResult currentResult) {
        this.allTokens.addAll(currentResult.allTokens);
    }

    public void addResult(Set<Token> vertexSet) {
        this.allTokens.addAll(vertexSet);
    }

    public void addResult(TokenExtractorResult tokenExtractorResult) {
        if (tokenExtractorResult != null) {
            this.allTokens.addAll(tokenExtractorResult.graph.vertexSet());
        }
    }
}

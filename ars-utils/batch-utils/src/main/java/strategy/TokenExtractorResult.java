package strategy;

import org.jgrapht.Graph;

import entities.Token;
import entities.TokenEdge;

public class TokenExtractorResult {
    public Graph<Token, TokenEdge> graph;
    public TokenExtractor tokenExtractor;

    public TokenExtractorResult(Graph<Token, TokenEdge> graph, TokenExtractor tokenExtractor) {
        this.graph = graph;
        this.tokenExtractor = tokenExtractor;
    }
}

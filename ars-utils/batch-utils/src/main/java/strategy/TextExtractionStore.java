package strategy;

import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;

import entities.MetaType;
import entities.Token;
import entities.TokenEdge;
import entities.TokenEntityType;

public class TextExtractionStore {
    public String inputText;
    public Map<MetaType, Object> inputMeta;

    public Map<String, TokenExtractorResult> extractorName2Result = new HashMap<>();
    public Graph<Token, TokenEdge> graph = new SimpleDirectedGraph<>(TokenEdge.class);
    public Map<TokenEntityType, List<Token>> entityType2Tokens = new HashMap<>();

    public void addExtractionResult(TokenExtractorResult result) {
        this.extractorName2Result.put(result.tokenExtractor.getName(), result);
        addGraph(result.graph);
    }

    public void addGraph(Graph<Token, TokenEdge> additionalGraph) {
        for (Token token : additionalGraph.vertexSet()) {
            addToken(token);
        }
        for (TokenEdge edge : additionalGraph.edgeSet()) {
            addEdge(additionalGraph.getEdgeSource(edge), additionalGraph.getEdgeTarget(edge), edge);
        }
    }

    public void addToken(Token token) {
        this.graph.addVertex(token);
        // if (this.entityType2Tokens.containsKey(token.entityType)) {
        //     this.entityType2Tokens.get(token.entityType).add(token);
        // } else {
        //     this.entityType2Tokens.put(token.entityType, Arrays.asList(token));
        // }
    }

    public void addEdge(Token source, Token target, TokenEdge edge) {
        addToken(source);
        addToken(target);
        graph.addEdge(source, target, edge);
    }

    public List<TokenExtractor> getAllExtractors() {
        return extractorName2Result.values().stream().map(r -> r.tokenExtractor).collect(Collectors.toList());
    }
}

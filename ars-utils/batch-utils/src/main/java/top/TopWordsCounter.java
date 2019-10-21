package top;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import utils.FileUtilsHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class TopWordsCounter {
    private static Set<String> defaultStopWords;
    private static Analyzer analyzerRu;
    private static Analyzer analyzerEn;
    private static Matcher engMatcher = Pattern.compile("[A-Za-z]+").matcher("");
    private Set<String> userStopWords;

    public TopWordsCounter(Set<String> userStopWords) {
        this.userStopWords = userStopWords;
        initialize();
    }

    private synchronized void initialize() {
        try {
            if (defaultStopWords == null) {
                defaultStopWords = new HashSet<>();
                List<String> lines = FileUtilsHelper.readLinesFromResource("dictionaries/stop_words.txt");
                defaultStopWords.addAll(lines);
            }

            if (analyzerEn == null) {
                analyzerEn = new CustomAnalyzer();
            }

            if (analyzerRu == null) {
                analyzerRu = new CustomAnalyzer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> topBiWords(String text, int size) {
        Map<String, Integer> topWords = new HashMap<>();

        getBiGramWords(text).forEach(w -> topWords.merge(w, 1, (o, n) -> o + n));

        return formatTopWords(topWords, size);
    }

    public Map<String, Integer> topWords(String text, int size) {
        Map<String, Integer> topWords = new HashMap<>();

        getNormalizedWords(text).forEach(w -> topWords.merge(w, 1, (o, n) -> o + n));

        return formatTopWords(topWords, size);
    }

    private Map<String, Integer> formatTopWords(Map<String, Integer> topWords, int size) {
        return topWords
            .entrySet()
            .stream()
            .sorted((o1, o2) -> {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return -1 * o1.getValue().compareTo(o2.getValue());
            })
            .limit(size)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<String> getNormalizedWords(String text) {
        List<String> wordsList = new ArrayList<>();
        try {
            // 1. Calculate russian-stem words
            TokenStream streamRu = analyzerRu.tokenStream(null, new StringReader(text));
            streamRu.reset();
            while (streamRu.incrementToken()) {
                String word = streamRu.getAttribute(CharTermAttribute.class).toString();
                if (isInvalidWord(word) || engMatcher.reset(word).find()) {
                    continue;
                }
                wordsList.add(word);
            }
            streamRu.end();
            streamRu.close();

            // 2. Calculate english-stem words
            TokenStream streamEn = analyzerEn.tokenStream(null, new StringReader(text));
            streamEn.reset();
            while (streamEn.incrementToken()) {
                String word = streamEn.getAttribute(CharTermAttribute.class).toString();
                if (isInvalidWord(word) || !engMatcher.reset(word).find()) {
                    continue;
                }
                wordsList.add(word);
            }
            streamEn.end();
            streamEn.close();

            return wordsList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getBiGramWords(String text) {
        String filteredText =
            Arrays.stream(text.split(" "))
                  .filter(p -> !isInvalidWord(p))
                  .collect(Collectors.joining(" "));

        List<String> biGramWords = new ArrayList<>();
        try (ShingleFilter shingleFilter = new ShingleFilter(buildTokenStream(filteredText))) {
            shingleFilter.setOutputUnigrams(false);
            shingleFilter.reset();
            while (shingleFilter.incrementToken()) {
                String word = shingleFilter.getAttribute(CharTermAttribute.class).toString();
                if (isInvalidWord(word)) {
                    continue;
                }
                biGramWords.add(word.toLowerCase());
            }
            shingleFilter.end();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return biGramWords;
    }

    private TokenStream buildTokenStream(String content) {
        Analyzer analyzer = new StandardAnalyzer();
        StringReader reader = new StringReader(content);
        return analyzer.tokenStream("content", reader);
    }

    private boolean isInvalidWord(String word) {
        // Skip short and only digit
        if (word.length() < 3 || word.matches("\\d+")) {
            return true;
        }

        return defaultStopWords.contains(word) || (userStopWords != null && userStopWords.contains(word));
    }
}

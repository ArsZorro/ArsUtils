package top;

import java.io.*;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilter;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

public final class CustomAnalyzer extends Analyzer {
    private static final CharArraySet STOPWORDS = new CharArraySet(170, false);
    private static LuceneMorphology luceneRussianMorph;
    private static LuceneMorphology luceneEnglishMorph;
    private final Logger logger = Logger.getLogger(this.getClass());
    private boolean keepKeyword = true;

    static {
        STOPWORDS.addAll(EnglishAnalyzer.getDefaultStopSet());
        STOPWORDS.addAll(RussianAnalyzer.getDefaultStopSet());
    }

    public CustomAnalyzer() {
        if (luceneRussianMorph == null && luceneEnglishMorph == null) {
            try {
                luceneRussianMorph = new RussianLuceneMorphology();
                luceneEnglishMorph = new EnglishLuceneMorphology();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public CustomAnalyzer(boolean keepKeyword) {
        this();
        this.keepKeyword = keepKeyword;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, STOPWORDS);
        if (keepKeyword) {
            tok = new KeywordRepeatFilter(tok);
        }
        if (luceneRussianMorph != null) {
            tok = new MorphologyFilter(tok, luceneRussianMorph);
        }
        if (luceneEnglishMorph != null) {
            tok = new MorphologyFilter(tok, luceneEnglishMorph);
        }
        if (keepKeyword) {
            tok = new RemoveDuplicatesTokenFilter(tok);
        }
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                try {
                    src.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                super.setReader(reader);
            }
        };
    }
}

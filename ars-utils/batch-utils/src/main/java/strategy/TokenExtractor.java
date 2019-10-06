package strategy;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class TokenExtractor {
    public Integer priority = 0;
    public boolean isActive = true;
    private String name;

    @JsonCreator
    public TokenExtractor(String name) {
        this.name = name;
    }

    public final TokenExtractorResult extract(String text) {
        return extract(text);
    }

    public String getName() {
        return name;
    }
}

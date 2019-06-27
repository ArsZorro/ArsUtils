package entities;

public class ThemeSentence {
    public Integer start;
    public Integer end;
    public String theme;

    public ThemeSentence(Integer start, Integer end, String theme) {
        this.start = start;
        this.end = end;
        this.theme = theme;
    }
}

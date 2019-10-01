package entities;

public class Token {
    public Integer start;
    public Integer end;
    public String theme;

    @Override
    public String toString() {
        return "start=" + start + " end=" + end + " theme=" + theme;
    }
}

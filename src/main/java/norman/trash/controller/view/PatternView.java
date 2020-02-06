package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;

public class PatternView {
    private Long id;
    private String catName;
    private Integer seq;
    private String regex;

    public Long getId() {
        return id;
    }

    public String getCatName() {
        return catName;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getRegex() {
        return regex;
    }

    public PatternView(Pattern pattern) {
        id = pattern.getId();
        Cat cat = pattern.getCat();
        if (cat != null) {
            catName = cat.getName();
        }
        seq = pattern.getSeq();
        regex = pattern.getRegex();
    }
}

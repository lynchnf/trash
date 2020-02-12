package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;

public class PatternView {
    private Long id;
    private Long catId;
    private String catName;
    private Integer seq;
    private String regex;

    public PatternView(Pattern pattern) {
        id = pattern.getId();
        Cat cat = pattern.getCat();
        if (cat != null) {
            catId = cat.getId();
            catName = cat.getName();
        }
        seq = pattern.getSeq();
        regex = pattern.getRegex();
    }

    public Long getId() {
        return id;
    }

    public Long getCatId() {
        return catId;
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
}

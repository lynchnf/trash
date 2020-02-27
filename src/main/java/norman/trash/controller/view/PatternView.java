package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;

public class PatternView {
    private Long id;
    private Integer seq;
    private String regex;
    private Long catId;
    private String catName;

    public PatternView(Pattern pattern) {
        id = pattern.getId();
        seq = pattern.getSeq();
        regex = pattern.getRegex();
        Cat cat = pattern.getCat();
        if (cat != null) {
            catId = cat.getId();
            catName = cat.getName();
        }
    }

    public Long getId() {
        return id;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getRegex() {
        return regex;
    }

    public Long getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }
}

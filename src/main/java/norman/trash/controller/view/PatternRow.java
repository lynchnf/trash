package norman.trash.controller.view;

import norman.trash.controller.view.validation.RegexPattern;
import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PatternRow {
    private Long id;
    private Integer version = 0;
    @NotBlank(message = "Regular Expression may not be blank.")
    @Size(max = 255, message = "Regular Expression may not be over {max} characters long.")
    @RegexPattern
    private String regex;
    @NotNull(message = "Category may not be blank.")
    private Long catId;

    public PatternRow() {
    }

    public PatternRow(Pattern pattern) {
        id = pattern.getId();
        version = pattern.getVersion();
        regex = pattern.getRegex();
        catId = pattern.getCat().getId();
    }

    public Pattern toPattern() {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setVersion(version);
        pattern.setRegex(regex);
        pattern.setCat(new Cat());
        pattern.getCat().setId(catId);
        return pattern;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }
}
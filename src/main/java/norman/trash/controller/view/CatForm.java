package norman.trash.controller.view;

import norman.trash.domain.Cat;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CatForm {
    private Long id;
    private Integer version = 0;
    @NotBlank(message = "Category Name may not be blank.")
    @Size(max = 100, message = "Category Name may not be over {max} characters long.")
    private String name;

    public CatForm() {
    }

    public CatForm(Cat cat) {
        id = cat.getId();
        version = cat.getVersion();
        name = cat.getName();
    }

    public Cat toCat() {
        Cat cat = new Cat();
        cat.setId(id);
        cat.setVersion(version);
        cat.setName(StringUtils.trimToNull(name));
        return cat;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

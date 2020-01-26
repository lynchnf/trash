package norman.trash.controller;

import norman.trash.domain.Cat;
import org.springframework.data.domain.Page;

public class CatListForm extends ListForm<Cat> {
    private String whereName;

    public CatListForm(Page<Cat> innerPage, String whereName) {
        super(innerPage);
        this.whereName = whereName;
    }

    public String getWhereName() {
        return whereName;
    }
}

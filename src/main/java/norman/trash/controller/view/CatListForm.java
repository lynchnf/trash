package norman.trash.controller.view;

import norman.trash.domain.Cat;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class CatListForm extends ListForm<Cat> {
    private String whereName;
    private List<CatView> rows = new ArrayList<>();

    public CatListForm(Page<Cat> innerPage, String whereName) {
        super(innerPage);
        this.whereName = whereName;
        for (Cat cat : innerPage.getContent()) {
            CatView row = new CatView(cat);
            rows.add(row);
        }
    }

    public String getWhereName() {
        return whereName;
    }

    public List<CatView> getRows() {
        return rows;
    }
}

package norman.trash.controller.view;

import norman.trash.domain.Tran;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class TranListForm extends ListForm<Tran> {
    private String whereName;
    private List<TranView> rows = new ArrayList<>();

    public TranListForm(Page<Tran> innerPage, String whereName) {
        super(innerPage);
        this.whereName = whereName;
        for (Tran tran : innerPage.getContent()) {
            TranView row = new TranView(tran);
            rows.add(row);
        }
    }

    public String getWhereName() {
        return whereName;
    }

    public List<TranView> getRows() {
        return rows;
    }
}

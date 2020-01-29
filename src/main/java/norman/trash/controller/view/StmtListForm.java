package norman.trash.controller.view;

import norman.trash.domain.Stmt;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class StmtListForm extends ListForm<Stmt> {
    private List<StmtView> rows = new ArrayList<>();

    public StmtListForm(Page<Stmt> innerPage) {
        super(innerPage);
        for (Stmt stmt : innerPage.getContent()) {
            StmtView row = new StmtView(stmt);
            rows.add(row);
        }
    }

    public List<StmtView> getRows() {
        return rows;
    }
}

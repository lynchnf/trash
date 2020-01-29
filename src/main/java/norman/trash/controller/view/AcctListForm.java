package norman.trash.controller.view;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class AcctListForm extends ListForm<Acct> {
    private String whereName;
    private AcctType whereType;
    private List<AcctView> rows = new ArrayList<>();

    public AcctListForm(Page<Acct> innerPage, String whereName, AcctType whereType) {
        super(innerPage);
        this.whereName = whereName;
        this.whereType = whereType;
        for (Acct acct : innerPage.getContent()) {
            AcctView row = new AcctView(acct);
            rows.add(row);
        }
    }

    public String getWhereName() {
        return whereName;
    }

    public AcctType getWhereType() {
        return whereType;
    }

    public List<AcctView> getRows() {
        return rows;
    }
}

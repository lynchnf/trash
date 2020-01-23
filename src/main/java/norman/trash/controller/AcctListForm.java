package norman.trash.controller;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.data.domain.Page;

public class AcctListForm extends ListForm<Acct> {
    private String whereName;
    private AcctType whereType;

    public AcctListForm(Page<Acct> innerPage, String whereName, AcctType whereType) {
        super(innerPage);
        this.whereName = whereName;
        this.whereType = whereType;
    }

    public String getWhereName() {
        return whereName;
    }

    public AcctType getWhereType() {
        return whereType;
    }
}

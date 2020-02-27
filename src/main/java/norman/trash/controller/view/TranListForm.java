package norman.trash.controller.view;

import norman.trash.domain.Tran;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class TranListForm extends ListForm<Tran> {
    private Long viewingAcctId;
    private List<TranView> rows = new ArrayList<>();

    public TranListForm(Page<Tran> innerPage, Long viewingAcctId) {
        super(innerPage);
        this.viewingAcctId = viewingAcctId;
        for (Tran tran : innerPage.getContent()) {
            TranView row = new TranView(tran);
            rows.add(row);
        }
    }

    public List<TranView> getRows() {
        return rows;
    }
}

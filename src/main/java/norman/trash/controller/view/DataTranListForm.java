package norman.trash.controller.view;

import norman.trash.domain.DataTran;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class DataTranListForm extends ListForm<DataTran> {
    private List<DataTranView> rows = new ArrayList<>();

    public DataTranListForm(Page<DataTran> innerPage) {
        super(innerPage);
        for (DataTran dataTran : innerPage.getContent()) {
            DataTranView row = new DataTranView(dataTran);
            rows.add(row);
        }
    }

    public List<DataTranView> getRows() {
        return rows;
    }
}

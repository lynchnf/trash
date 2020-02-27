package norman.trash.controller.view;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class DataTranMatchForm {
    @Valid
    private List<DataTranMatchRow> rows = new ArrayList<>();

    public DataTranMatchForm() {
    }

    public DataTranMatchForm(List<DataTranMatchRow> rows) {
        rows.addAll(rows);
    }

    public List<DataTranMatchRow> getRows() {
        return rows;
    }

    public void setRows(List<DataTranMatchRow> rows) {
        this.rows = rows;
    }
}

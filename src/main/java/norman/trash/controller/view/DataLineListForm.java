package norman.trash.controller.view;

import norman.trash.domain.DataLine;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class DataLineListForm extends ListForm<DataLine> {
    private List<DataLineView> rows = new ArrayList<>();

    public DataLineListForm(Page<DataLine> innerPage) {
        super(innerPage);
        for (DataLine dataLine : innerPage.getContent()) {
            DataLineView row = new DataLineView(dataLine);
            rows.add(row);
        }
    }

    public List<DataLineView> getRows() {
        return rows;
    }
}

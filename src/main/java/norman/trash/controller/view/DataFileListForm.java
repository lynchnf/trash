package norman.trash.controller.view;

import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class DataFileListForm extends ListForm<DataFile> {
    private String whereOriginalFilename;
    private DataFileStatus whereStatus;
    private List<DataFileView> rows = new ArrayList<>();

    public DataFileListForm(Page<DataFile> innerPage, String whereOriginalFilename, DataFileStatus whereStatus) {
        super(innerPage);
        this.whereOriginalFilename = whereOriginalFilename;
        this.whereStatus = whereStatus;
        for (DataFile dataFile : innerPage.getContent()) {
            DataFileView row = new DataFileView(dataFile);
            rows.add(row);
        }
    }

    public String getWhereOriginalFilename() {
        return whereOriginalFilename;
    }

    public DataFileStatus getWhereStatus() {
        return whereStatus;
    }

    public List<DataFileView> getRows() {
        return rows;
    }
}

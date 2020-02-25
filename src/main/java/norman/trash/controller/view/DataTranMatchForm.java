package norman.trash.controller.view;

import norman.trash.domain.Acct;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;

import java.util.Date;

public class DataTranMatchForm {
    private Long id;
    private Integer version = 0;
    private String originalFilename;
    private Date uploadTimestamp;
    private DataFileStatus status;

    public DataTranMatchForm() {
    }

    public DataTranMatchForm(DataFile dataFile, Acct acct) {

    }
}

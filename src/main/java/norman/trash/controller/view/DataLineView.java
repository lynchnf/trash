package norman.trash.controller.view;

import norman.trash.domain.DataLine;

public class DataLineView {
    private Long id;
    private Integer seq;
    private String text;
    private Long dataFileId;
    private String dataFileOriginalFilename;

    public DataLineView(DataLine dataLine) {
        id = dataLine.getId();
        seq = dataLine.getSeq();
        text = dataLine.getText();
        dataFileId = dataLine.getDataFile().getId();
        dataFileOriginalFilename = dataLine.getDataFile().getOriginalFilename();
    }

    public Long getId() {
        return id;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getText() {
        return text;
    }

    public Long getDataFileId() {
        return dataFileId;
    }

    public String getDataFileOriginalFilename() {
        return dataFileOriginalFilename;
    }
}

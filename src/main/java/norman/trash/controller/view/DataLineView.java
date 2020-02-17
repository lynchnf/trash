package norman.trash.controller.view;

import norman.trash.domain.DataLine;

public class DataLineView {
    private Long id;
    private Integer seq;
    private String text;

    public DataLineView(DataLine dataLine) {
        id = dataLine.getId();
        seq = dataLine.getSeq();
        text = dataLine.getText();
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
}

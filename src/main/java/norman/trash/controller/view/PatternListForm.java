package norman.trash.controller.view;

import norman.trash.domain.Pattern;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PatternListForm extends ListForm<Pattern> {
    private String whereRegex;
    private List<PatternView> rows = new ArrayList<>();

    public PatternListForm(Page<Pattern> innerPage, String whereRegex) {
        super(innerPage);
        this.whereRegex = whereRegex;
        for (Pattern pattern : innerPage.getContent()) {
            PatternView row = new PatternView(pattern);
            rows.add(row);
        }
    }

    public String getWhereRegex() {
        return whereRegex;
    }

    public List<PatternView> getRows() {
        return rows;
    }
}

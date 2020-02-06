package norman.trash.controller.view;

import norman.trash.domain.Pattern;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class PatternForm {
    @Valid
    private List<PatternRow> patternRows = new ArrayList<>();
    private PatternFormAction action;

    public PatternForm() {
    }

    public PatternForm(Iterable<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            PatternRow patternRow = new PatternRow(pattern);
            patternRows.add(patternRow);
        }
    }

    public List<PatternRow> getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(List<PatternRow> patternRows) {
        this.patternRows = patternRows;
    }
}
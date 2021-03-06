package norman.trash.controller.view;

import norman.trash.domain.Pattern;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class PatternForm {
    @Valid
    private List<PatternRow> patternRows = new ArrayList<>();
    private List<Long> idList = new ArrayList<>();

    public PatternForm() {
    }

    public PatternForm(Iterable<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            PatternRow patternRow = new PatternRow(pattern);
            patternRows.add(patternRow);
            idList.add(pattern.getId());
        }
    }

    public List<Pattern> toPatterns() {
        List<Pattern> patterns = new ArrayList<>();
        for (int i = 0; i < patternRows.size(); i++) {
            PatternRow patternRow = patternRows.get(i);
            Pattern pattern = patternRow.toPattern();
            pattern.setSeq(i + 1);
            patterns.add(pattern);
        }
        return patterns;
    }

    public List<PatternRow> getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(List<PatternRow> patternRows) {
        this.patternRows = patternRows;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatternForm {
    @Valid
    private List<PatternRow> patternRows = new ArrayList<>();
    private Map<Long, String> catMap = new LinkedHashMap<>();

    public PatternForm() {
    }

    public PatternForm(Iterable<Pattern> patterns, Iterable<Cat> cats) {
        for (Pattern pattern : patterns) {
            PatternRow patternRow = new PatternRow(pattern);
            patternRows.add(patternRow);
        }
        for (Cat cat : cats) {
            catMap.put(cat.getId(), cat.getName());
        }
    }

    public List<PatternRow> getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(List<PatternRow> patternRows) {
        this.patternRows = patternRows;
    }

    public Map<Long, String> getCatMap() {
        return catMap;
    }

    public void setCatMap(Map<Long, String> catMap) {
        this.catMap = catMap;
    }
}
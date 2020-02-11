package norman.trash.controller;

import norman.trash.MultipleOptimisticLockingException;
import norman.trash.controller.view.PatternForm;
import norman.trash.controller.view.PatternListForm;
import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;
import norman.trash.service.CatService;
import norman.trash.service.PatternService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static norman.trash.MessagesConstants.MULTIPLE_SUCCESSFULLY_UPDATED;

@Controller
public class PatternController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] catSortableColumns = {"seq", "regex"};
    @Autowired
    private PatternService patternService;
    @Autowired
    private CatService catService;

    @GetMapping("/patternList")
    // @formatter:off
    public String loadPatternList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "seq") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            @RequestParam(value = "whereRegex", required = false) String whereRegex) {
        // @formatter:on
        String trimmedRegex = StringUtils.trimToNull(whereRegex);

        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(catSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        // Get a page of records.
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<Pattern> page = null;
        if (trimmedRegex != null) {
            page = patternService.findByRegex(trimmedRegex, pageable);
        } else {
            page = patternService.findAll(pageable);
        }
        model.addAttribute("listForm", new PatternListForm(page, trimmedRegex));
        return "patternList";
    }

    @GetMapping("/patternEdit")
    public String loadPatternEdit(Model model) {
        Iterable<Pattern> patterns = patternService.findAll();
        PatternForm patternForm = new PatternForm(patterns);
        model.addAttribute("patternForm", patternForm);
        return "patternEdit";
    }

    @PostMapping("/patternEdit")
    private String processPatternEdit(@Valid PatternForm patternForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "patternEdit";
        }

        List<Pattern> patterns = patternForm.toPatterns();

        try {
            patternService.saveAll(patterns, patternForm.getIdList());
            String successMessage = String.format(MULTIPLE_SUCCESSFULLY_UPDATED, "Patterns");
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/patternList";
        } catch (MultipleOptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patternList";
        }
    }

    @ModelAttribute("allCats")
    public Iterable<Cat> loadCatsDropDown() {
        return catService.findAll();
    }
}
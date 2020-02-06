package norman.trash.controller;

import norman.trash.controller.view.PatternListForm;
import norman.trash.domain.Pattern;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
public class PatternController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] catSortableColumns = {"seq", "regex"};
    @Autowired
    private PatternService patternService;

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

/*
    @GetMapping("/patternEdit")
    public String loadEdit(Model model) {
        Iterable<Pattern> patterns = patternService.findAllPatterns();
        norman.junk.controller.PatternForm patternForm = new PatternForm(patterns);
        model.addAttribute("patternForm", patternForm);
        return "patternEdit";
    }

    @PostMapping("/patternEdit")
    public String processEdit(@Valid PatternForm patternForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "patternEdit";
        }
        // Convert form to entities and save.
        List<Pattern> patterns = patternForm.toPatterns(categoryService);
        try {
            patternService.saveAllPatterns(patterns);
            String successMessage = String.format(SUCCESSFULLY_UPDATED_MULTI, "Patterns");
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/patternList";
        } catch (JunkOptimisticLockingException e) {
            String msg = String.format(MULTI_OPTIMISTIC_LOCK_ERROR, "Patterns");
            logger.warn(msg, e);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/patternList";
        }
    }

    @ModelAttribute("allCategories")
    public Iterable<Category> loadCategoryDropDown() {
        return categoryService.findAllCategories();
    }
*/
}
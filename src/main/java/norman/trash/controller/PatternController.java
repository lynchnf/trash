package norman.trash.controller;

import norman.trash.MultipleOptimisticLockingException;
import norman.trash.NotFoundException;
import norman.trash.controller.view.PatternForm;
import norman.trash.controller.view.PatternListForm;
import norman.trash.controller.view.PatternRow;
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
import java.util.ArrayList;
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
        Iterable<Cat> cats = catService.findAll();
        PatternForm patternForm = new PatternForm(patterns, cats);
        model.addAttribute("patternForm", patternForm);
        return "patternEdit";
    }

    @PostMapping("/patternEdit")
    private String processPatternEdit(@Valid PatternForm patternForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "patternEdit";
        }

        try {
            List<Pattern> patterns = new ArrayList<>();
            for (int i = 0; i < patternForm.getPatternRows().size(); i++) {
                if (i != 2) {
                    PatternRow patternRow = patternForm.getPatternRows().get(i);
                    Pattern pattern = new Pattern();
                    pattern.setId(patternRow.getId());
                    pattern.setVersion(patternRow.getVersion());
                    pattern.setRegex(patternRow.getRegex());
                    Long catId = patternRow.getCatId();
                    Cat cat = catService.findById(catId);
                    pattern.setCat(cat);
                    pattern.setSeq(i + 1);
                    patterns.add(pattern);
                }
            }
            patternService.saveAll(patterns);
            String successMessage = String.format(MULTIPLE_SUCCESSFULLY_UPDATED, "Patterns");
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/patternList";
        } catch (NotFoundException | MultipleOptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/patternList";
        }
    }

    @ModelAttribute("allCats")
    public Iterable<Cat> loadCatsDropDown() {
        return catService.findAll();
    }

/*
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
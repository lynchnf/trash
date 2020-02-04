package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.OptimisticLockingException;
import norman.trash.controller.view.CatForm;
import norman.trash.controller.view.CatListForm;
import norman.trash.controller.view.CatView;
import norman.trash.domain.Cat;
import norman.trash.service.CatService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

import static norman.trash.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.trash.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class CatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] catSortableColumns = {"name"};
    @Autowired
    private CatService catService;

    @GetMapping("/catList")
    // @formatter:off
    public String loadCatList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "name") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            @RequestParam(value = "whereName", required = false) String whereName) {
        // @formatter:on
        String trimmedName = StringUtils.trimToNull(whereName);

        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(catSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        // Get a page of records.
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<Cat> page = null;
        if (trimmedName != null) {
            page = catService.findByName(trimmedName, pageable);
        } else {
            page = catService.findAll(pageable);
        }
        model.addAttribute("listForm", new CatListForm(page, whereName));
        return "catList";
    }

    @GetMapping("/cat")
    public String loadCatView(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cat cat = catService.findById(id);
            CatView view = new CatView(cat);
            model.addAttribute("view", view);
            return "catView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/catList";
        }
    }

    @GetMapping("/catEdit")
    public String loadCatEdit(@RequestParam(value = "id", required = false) Long id, Model model,
            RedirectAttributes redirectAttributes) {

        // If no id, new category.
        if (id == null) {
            model.addAttribute("catForm", new CatForm());
            return "catEdit";
        }

        // Otherwise, edit existing category.
        try {
            Cat cat = catService.findById(id);
            CatForm catForm = new CatForm(cat);
            model.addAttribute("catForm", catForm);
            return "catEdit";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/catList";
        }
    }

    @PostMapping("/catEdit")
    public String processCatEdit(@Valid CatForm catForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "catEdit";
        }

        // Convert form to entity.
        Long catId = catForm.getId();
        Cat cat = catForm.toCat();

        // Save entity.
        try {
            Cat save = catService.save(cat);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Category", save.getId());
            if (catId != null) {
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Category", save.getId());
            }
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/cat?id={id}";
        } catch (OptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/catList";
        }
    }
}

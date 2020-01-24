package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.service.AcctService;
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

import static norman.trash.controller.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.trash.controller.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class AcctController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] otherSortableColumns = {"name", "type"};
    @Autowired
    private AcctService service;

    @GetMapping("/acctList")
    // @formatter:off
    public String loadList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "name") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            @RequestParam(value = "whereName", required = false) String whereName,
            @RequestParam(value = "whereType", required = false) AcctType whereType) {
        // @formatter:on
        String trimmedName = StringUtils.trimToNull(whereName);

        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(otherSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        // Get a page of records.
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<Acct> page = null;
        if (trimmedName != null && whereType != null) {
            page = service.findByNameContainingAndType(trimmedName, whereType, pageable);
        } else if (trimmedName != null && whereType == null) {
            page = service.findByNameContaining(trimmedName, pageable);
        } else if (trimmedName == null && whereType != null) {
            page = service.findByType(whereType, pageable);
        } else {
            page = service.findAll(pageable);
        }

        AcctListForm listForm = new AcctListForm(page, whereName, whereType);
        model.addAttribute("listForm", listForm);
        return "acctList";
    }

    @GetMapping("/acct")
    public String loadView(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Acct acct = service.findById(id);
            model.addAttribute("acct", acct);
            return "acctView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/acctEdit")
    public String loadEdit(@RequestParam(value = "id", required = false) Long id, Model model,
            RedirectAttributes redirectAttributes) {

        // If no acct id, new acct.
        if (id == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }

        // Otherwise, edit existing acct.
        try {
            Acct acct = service.findById(id);
            AcctForm acctForm = new AcctForm(acct);
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @PostMapping("/acctEdit")
    public String processEdit(@Valid AcctForm acctForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }

        // Convert form to entity ...
        Long acctId = acctForm.getId();
        Acct acct = acctForm.toAcct();

        // ... and save.
        Acct save = null;
        // TODO Handle optimistic lock error
        save = service.save(acct);
        String successMessage = String.format(SUCCESSFULLY_ADDED, "Account", save.getId());
        if (acctId != null) {
            successMessage = String.format(SUCCESSFULLY_UPDATED, "Account", save.getId());
        }
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("id", save.getId());
        return "redirect:/acct?id={id}";
    }
}

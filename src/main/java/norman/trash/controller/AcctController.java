package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.controller.view.*;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.service.AcctService;
import norman.trash.service.StmtService;
import norman.trash.service.TranService;
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
import java.util.Calendar;

import static norman.trash.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.trash.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class AcctController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] acctSortableColumns = {"name", "type"};
    private static final String[] stmtSortableColumns = {"closeDate"};
    private static final String[] tranSortableColumns = {"postDate"};
    @Autowired
    private AcctService acctService;
    @Autowired
    private StmtService stmtService;
    @Autowired
    private TranService tranService;

    @GetMapping("/acctList")
    // @formatter:off
    public String loadAcctList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "name") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model,
            @RequestParam(value = "whereName", required = false) String whereName,
            @RequestParam(value = "whereType", required = false) AcctType whereType) {
        // @formatter:on
        String trimmedName = StringUtils.trimToNull(whereName);

        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(acctSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        // Get a page of records.
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<Acct> page = null;
        if (trimmedName != null && whereType != null) {
            page = acctService.findByNameContainingAndType(trimmedName, whereType, pageable);
        } else if (trimmedName != null && whereType == null) {
            page = acctService.findByNameContaining(trimmedName, pageable);
        } else if (trimmedName == null && whereType != null) {
            page = acctService.findByType(whereType, pageable);
        } else {
            page = acctService.findAll(pageable);
        }
        model.addAttribute("listForm", new AcctListForm(page, whereName, whereType));
        return "acctList";
    }

    @GetMapping("/acct")
    // @formatter:off
    public String loadAcctView(@RequestParam("id") Long id,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "12") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "closeDate") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on
        try {
            Acct acct = acctService.findById(id);
            AcctView view = new AcctView(acct);
            model.addAttribute("view", view);

            // Convert sort column from string to an array of strings.
            String[] sortColumns = {defaultSortColumn};
            if (Arrays.asList(stmtSortableColumns).contains(sortColumn)) {
                sortColumns = new String[]{sortColumn, defaultSortColumn};
            }

            PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
            Page<Stmt> page = stmtService.findByAcct_Id(id, pageable);
            StmtListForm listForm = new StmtListForm(page);
            model.addAttribute("listForm", listForm);
            return "acctView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/stmt")
    // @formatter:off
    public String loadStmtView(@RequestParam("id") Long id,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "postDate") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on
        try {
            Stmt stmt = stmtService.findById(id);
            StmtView view = new StmtView(stmt);
            model.addAttribute("view", view);

            // Convert sort column from string to an array of strings.
            String[] sortColumns = {defaultSortColumn};
            if (Arrays.asList(tranSortableColumns).contains(sortColumn)) {
                sortColumns = new String[]{sortColumn, defaultSortColumn};
            }

            PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
            Page<Tran> page = tranService.findByDebitStmt_IdOrCreditStmt_Id(id, id, pageable);
            TranListForm listForm = new TranListForm(page, stmt.getAcct().getId());
            model.addAttribute("listForm", listForm);
            return "stmtView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/tran")
    public String loadTranView(@RequestParam("id") Long id,
            @RequestParam(value = "viewingAcctId", required = false) Long viewingAcctId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Tran tran = tranService.findById(id);
            TranView view = new TranView(tran, viewingAcctId);
            model.addAttribute("view", view);
            return "tranView";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/acctEdit")
    public String loadAcctEdit(@RequestParam(value = "id", required = false) Long id, Model model,
            RedirectAttributes redirectAttributes) {

        // If no acct id, new acct.
        if (id == null) {
            model.addAttribute("acctForm", new AcctForm());
            return "acctEdit";
        }

        // Otherwise, edit existing acct.
        try {
            Acct acct = acctService.findById(id);
            AcctForm acctForm = new AcctForm(acct);
            model.addAttribute("acctForm", acctForm);
            return "acctEdit";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @PostMapping("/acctEdit")
    public String processAcctEdit(@Valid AcctForm acctForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctEdit";
        }

        // Convert form to entity ...
        Long acctId = acctForm.getId();
        Acct acct = acctForm.toAcct();

        // If new acct, create a statement too.
        if (acctId == null) {
            Stmt stmt = new Stmt();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, 9999);
            stmt.setCloseDate(cal.getTime());
            stmt.setAcct(acct);
            acct.getStmts().add(stmt);
        }

        // ... and save.
        Acct save = null;
        // TODO Handle optimistic lock error
        save = acctService.save(acct);
        String successMessage = String.format(SUCCESSFULLY_ADDED, "Account", save.getId());
        if (acctId != null) {
            successMessage = String.format(SUCCESSFULLY_UPDATED, "Account", save.getId());
        }
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        redirectAttributes.addAttribute("id", save.getId());
        return "redirect:/acct?id={id}";
    }
}

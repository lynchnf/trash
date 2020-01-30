package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.controller.view.*;
import norman.trash.domain.*;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

import static norman.trash.MessagesConstants.*;

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
            page = acctService.findByNameAndType(trimmedName, whereType, pageable);
        } else if (trimmedName != null && whereType == null) {
            page = acctService.findByName(trimmedName, pageable);
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
            Page<Stmt> page = stmtService.findByAcctId(id, pageable);
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
            Page<Tran> page = tranService.findByStmtId(id, pageable);
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

        // Convert form to entity.
        Long acctId = acctForm.getId();
        Acct acct = acctForm.toAcct();

        // If this is a new acct, we also need an acct number, a current period statement and a beginning statement and
        // transaction.
        if (acctId == null) {
            // Account number.
            AcctNbr acctNbr = acctForm.toAcctNbr();
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);

            // Current period statement.
            Stmt currentStmt = new Stmt();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, 9999);
            currentStmt.setCloseDate(cal.getTime());
            currentStmt.setAcct(acct);
            acct.getStmts().add(currentStmt);

            // Beginning period statement.
            Stmt beginningStmt = new Stmt();
            beginningStmt.setCloseDate(acctForm.getEffDate());
            beginningStmt.setAcct(acct);
            acct.getStmts().add(beginningStmt);

            // Beginning transaction.
            Tran tran = new Tran();
            tran.setPostDate(acctForm.getEffDate());
            BigDecimal amount = acctForm.getBeginningBalance();
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                tran.setAmount(amount.negate());
                tran.setDebitStmt(beginningStmt);
                beginningStmt.getDebitTrans().add(tran);
            } else {
                tran.setAmount(amount);
                tran.setCreditStmt(beginningStmt);
                beginningStmt.getCreditTrans().add(tran);
            }
            tran.setName(BEGINNING_BALANCE);

            // Otherwise, this is an existing account. If either the acctNumber or effective date changed, we need to
            // save the account number with the account.
        } else if (!acctForm.getNumber().equals(acctForm.getOldNumber()) ||
                !acctForm.getEffDate().equals(acctForm.getOldEffDate())) {
            AcctNbr acctNbr = acctForm.toAcctNbr();
            acctNbr.setAcct(acct);
            acct.getAcctNbrs().add(acctNbr);
        }

        // Save entity.
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

package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.OptimisticLockingException;
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
import java.util.*;

import static norman.trash.MessagesConstants.*;
import static norman.trash.controller.view.BalanceType.*;

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
    private Date endOfTime;

    public AcctController() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        endOfTime = cal.getTime();
    }

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
            currentStmt.setCloseDate(endOfTime);
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
        try {
            Acct save = acctService.save(acct);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Account", save.getId());
            if (acctId != null) {
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Account", save.getId());
            }
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/acct?id={id}";
        } catch (OptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/acctBalance")
    public String loadAcctBalance(@RequestParam(value = "id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Acct acct = acctService.findById(id);
            AcctView view = new AcctView(acct);
            model.addAttribute("view", view);

            // Load rows.
            List<BalanceView> rows = new ArrayList<>();
            for (Stmt stmt : acct.getStmts()) {
                BalanceView stmtRow = new BalanceView(stmt);
                rows.add(stmtRow);

                for (Tran tran : stmt.getDebitTrans()) {
                    BalanceView debitRow = new BalanceView(tran, DEBIT_TRAN);
                    rows.add(debitRow);
                }
                for (Tran tran : stmt.getCreditTrans()) {
                    BalanceView creditTran = new BalanceView(tran, CREDIT_TRAN);
                    rows.add(creditTran);
                }
            }

            // Sort rows.
            Comparator<BalanceView> comparator = new Comparator<BalanceView>() {
                public int compare(BalanceView bal1, BalanceView bal2) {
                    int dateCompare = bal1.getPostDate().compareTo(bal2.getPostDate());
                    if (dateCompare != 0) {
                        return dateCompare;
                    }
                    if (bal1.getType() == STMT && (bal2.getType() == DEBIT_TRAN || bal2.getType() == CREDIT_TRAN)) {
                        return 1;
                    } else if ((bal1.getType() == DEBIT_TRAN || bal1.getType() == CREDIT_TRAN) &&
                            bal2.getType() == STMT) {
                        return -1;
                    }
                    return bal1.getId().compareTo(bal2.getId());
                }
            };
            rows.sort(comparator);

            // Update balance of rows.
            BigDecimal balance = BigDecimal.ZERO;
            for (BalanceView row : rows) {
                if (row.getType() != STMT) {
                    balance = balance.add(row.getAmount());
                }
                row.setBalance(balance);
            }

            Collections.reverse(rows);
            model.addAttribute("rows", rows);

            return "acctBalance";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @GetMapping("/acctReconcile")
    public String loadAcctReconcile(@RequestParam(value = "id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Acct acct = acctService.findById(id);
            AcctReconcileForm acctReconcileForm = new AcctReconcileForm(acct);
            model.addAttribute("acctReconcileForm", acctReconcileForm);
            return "acctReconcile";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @PostMapping("/acctReconcile")
    public String processAcctReconcile(@Valid AcctReconcileForm acctReconcileForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "acctReconcile";
        }

        Long acctId = acctReconcileForm.getId();
        try {
            Acct acct = acctService.findById(acctId);
            acct.setVersion(acctReconcileForm.getVersion());

            // Update current statement to be the reconciled statement.
            for (Stmt stmt : acct.getStmts()) {
                if (stmt.getCloseDate().equals(endOfTime)) {
                    stmt.setOpenBalance(acctReconcileForm.getOpenBalance());
                    stmt.setDebits(acctReconcileForm.getDebits());
                    stmt.setCredits(acctReconcileForm.getCredits());
                    stmt.setFees(acctReconcileForm.getFees());
                    stmt.setInterest(acctReconcileForm.getInterest());
                    stmt.setCloseBalance(acctReconcileForm.getCloseBalance());
                    stmt.setMinimumDue(acctReconcileForm.getMinimumDue());
                    stmt.setDueDate(acctReconcileForm.getDueDate());
                    stmt.setCloseDate(acctReconcileForm.getCloseDate());
                }
            }
            // New current period statement.
            Stmt currentStmt = new Stmt();
            currentStmt.setCloseDate(endOfTime);
            currentStmt.setAcct(acct);
            acct.getStmts().add(currentStmt);

            Acct save = acctService.save(acct);
            String successMessage = String.format(SUCCESSFULLY_RECONCILED, save.getId());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/acct?id={id}";
        } catch (NotFoundException | OptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }
}

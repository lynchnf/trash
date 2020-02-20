package norman.trash.controller;

import norman.trash.TrashUtils;
import norman.trash.controller.view.*;
import norman.trash.domain.*;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OptimisticLockingException;
import norman.trash.service.AcctService;
import norman.trash.service.DataFileService;
import norman.trash.service.StmtService;
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
import static norman.trash.controller.view.BalanceType.CREDIT_TRAN;
import static norman.trash.controller.view.BalanceType.DEBIT_TRAN;

@Controller
public class AcctController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] acctSortableColumns = {"name", "type"};
    private static final String[] stmtSortableColumns = {"closeDate"};
    @Autowired
    private AcctService acctService;
    @Autowired
    private StmtService stmtService;
    @Autowired
    private DataFileService dataFileService;

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
        AcctListForm listForm = new AcctListForm(page, whereName, whereType);
        model.addAttribute("listForm", listForm);
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

    @GetMapping("/acctEdit")
    // @formatter:off
    public String loadAcctEdit(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "dataFileId", required = false) Long dataFileId,
            Model model,
            RedirectAttributes redirectAttributes) {
        // @formatter:on

        Acct acct = null;
        DataFile dataFile = null;
        try {
            if (id != null) {
                acct = acctService.findById(id);
            }
            if (dataFileId != null) {
                dataFile = dataFileService.findById(dataFileId);
            }

            AcctForm acctForm = new AcctForm(acct, dataFile);
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
        Long dataFileId = acctForm.getDataFileId();
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
            currentStmt.setCloseDate(TrashUtils.getEndOfTime());
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

        try {
            // If this came from an uploaded data file, link it to the account.
            if (dataFileId != null) {
                DataFile dataFile = dataFileService.findById(dataFileId);
                dataFile.setStatus(DataFileStatus.AC_MATCHED);
                dataFile.setAcct(acct);
                acct.getDataFiles().add(dataFile);
            }

            // Save entity.
            Acct save = acctService.save(acct);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Account", save.getId());
            if (acctId != null) {
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Account", save.getId());
            }
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/acct?id={id}";
        } catch (NotFoundException | OptimisticLockingException e) {
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
                    return bal1.getId().compareTo(bal2.getId());
                }
            };
            rows.sort(comparator);

            // Update balance of rows.
            BigDecimal balance = BigDecimal.ZERO;
            for (BalanceView row : rows) {
                balance = balance.add(row.getAmount());
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
}

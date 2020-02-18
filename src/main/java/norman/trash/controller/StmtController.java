package norman.trash.controller;

import norman.trash.controller.view.StmtReconcileForm;
import norman.trash.controller.view.StmtView;
import norman.trash.controller.view.TranListForm;
import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.exception.MultipleOptimisticLockingException;
import norman.trash.exception.NotFoundException;
import norman.trash.service.AcctService;
import norman.trash.service.CatService;
import norman.trash.service.StmtService;
import norman.trash.service.TranService;
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
public class StmtController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StmtController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] tranSortableColumns = {"postDate"};
    @Autowired
    private AcctService acctService;
    @Autowired
    private StmtService stmtService;
    @Autowired
    private TranService tranService;
    @Autowired
    private CatService catService;

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

    @GetMapping("/stmtReconcile")
    public String loadStmtReconcile(@RequestParam(value = "id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Stmt stmt = stmtService.findById(id);
            StmtReconcileForm stmtReconcileForm = new StmtReconcileForm(stmt);
            model.addAttribute("stmtReconcileForm", stmtReconcileForm);
            return "stmtReconcile";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @PostMapping("/stmtReconcile")
    public String processStmtReconcile(@Valid StmtReconcileForm stmtReconcileForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "stmtReconcile";
        }
        Long acctId = stmtReconcileForm.getAcctId();
        List<Stmt> stmts = stmtReconcileForm.toStmts();
        try {
            stmtService.saveAll(stmts);
            String successMessage = String.format(MULTIPLE_SUCCESSFULLY_UPDATED, "Statements");
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", acctId);
            return "redirect:/acct?id={id}";
        } catch (MultipleOptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/acctList";
        }
    }

    @ModelAttribute("allCats")
    public Iterable<Cat> loadCatsDropDown() {
        return catService.findAll();
    }
}

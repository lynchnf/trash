package norman.trash.controller;

import norman.trash.TrashUtils;
import norman.trash.controller.view.TranForm;
import norman.trash.controller.view.TranView;
import norman.trash.domain.Acct;
import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OptimisticLockingException;
import norman.trash.service.AcctService;
import norman.trash.service.CatService;
import norman.trash.service.StmtService;
import norman.trash.service.TranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static norman.trash.MessagesConstants.SUCCESSFULLY_ADDED;
import static norman.trash.MessagesConstants.SUCCESSFULLY_UPDATED;

@Controller
public class TranController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranController.class);
    @Autowired
    private AcctService acctService;
    @Autowired
    private StmtService stmtService;
    @Autowired
    private TranService tranService;
    @Autowired
    private CatService catService;

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

    @GetMapping("/tranEdit")
    public String loadTranEdit(@RequestParam(value = "id", required = false) Long id, Model model,
            RedirectAttributes redirectAttributes) {

        // If no tran id, new tran.
        if (id == null) {
            model.addAttribute("tranForm", new TranForm());
            return "tranEdit";
        }

        // Otherwise, edit existing tran.
        try {
            Tran tran = tranService.findById(id);
            TranForm tranForm = new TranForm(tran);
            model.addAttribute("tranForm", tranForm);
            return "tranEdit";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tranList";
        }
    }

    @PostMapping("/tranEdit")
    public String processTranEdit(@Valid TranForm tranForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tranEdit";
        }

        // Convert form to entity.
        Long tranId = tranForm.getId();
        Tran tran = tranForm.toTran();

        Stmt debitStmt = tran.getDebitStmt();
        if (debitStmt != null) {
            Long acctId = debitStmt.getAcct().getId();
            Stmt stmt = stmtService.findByAcctIdAndCloseDate(acctId, TrashUtils.getEndOfTime());
            tran.setDebitStmt(stmt);
        }

        Stmt creditStmt = tran.getCreditStmt();
        if (creditStmt != null) {
            Long acctId = creditStmt.getAcct().getId();
            Stmt stmt = stmtService.findByAcctIdAndCloseDate(acctId, TrashUtils.getEndOfTime());
            tran.setCreditStmt(stmt);
        }

        Cat cat = tran.getCat();
        if (cat == null) {
            Cat foundCat = catService.findByPattern(tran.getName());
            if (foundCat != null) {
                tran.setCat(foundCat);
            }
        }

        // Save entity.
        try {
            Tran save = tranService.save(tran);
            String successMessage = String.format(SUCCESSFULLY_ADDED, "Transaction", save.getId());
            if (tranId != null) {
                successMessage = String.format(SUCCESSFULLY_UPDATED, "Transaction", save.getId());
            }
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            redirectAttributes.addAttribute("id", save.getId());
            return "redirect:/tran?id={id}";
        } catch (OptimisticLockingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tranList";
        }
    }

    @ModelAttribute("allAccts")
    public Iterable<Acct> loadAcctsDropDown() {
        return acctService.findAll();
    }

    @ModelAttribute("allCats")
    public Iterable<Cat> loadCatsDropDown() {
        return catService.findAll();
    }
}

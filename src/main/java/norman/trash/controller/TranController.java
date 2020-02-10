package norman.trash.controller;

import norman.trash.NotFoundException;
import norman.trash.controller.view.TranView;
import norman.trash.domain.Tran;
import norman.trash.service.TranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TranController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranController.class);
    @Autowired
    private TranService tranService;

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
}

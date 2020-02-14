package norman.trash.controller;

import norman.trash.controller.view.AcctView;
import norman.trash.domain.Acct;
import norman.trash.service.AcctService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    @Autowired
    private AcctService acctService;

    @GetMapping("/")
    public String loadDashboard(Model model) {
        Iterable<Acct> accts = acctService.findAll();
        LOGGER.debug("accts.size=" + accts);
        List<AcctView> acctRows = new ArrayList<>();
        for (Acct acct : accts) {
            AcctView acctRow = new AcctView(acct);
            acctRows.add(acctRow);
        }
        model.addAttribute("acctRows", acctRows);

        List<String> lines = new ArrayList<>();
        lines.add("This is a test.");
        lines.add("This is only a test.");
        model.addAttribute("lines", lines);
        return "index";
    }
}

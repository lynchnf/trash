package norman.trash.controller;

import norman.trash.domain.Acct;
import norman.trash.service.AcctService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcctController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctController.class);
    @Autowired
    private AcctService service;

    @GetMapping("/acctList")
    public String loadList(Model model) {
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.ASC;
        String[] properties = {"id", "name"};
        Pageable pageable = PageRequest.of(page, size, direction, properties);
        Page<Acct> accts = service.findAll(pageable);
        model.addAttribute("accts", accts);
        return "acctList";
    }
}

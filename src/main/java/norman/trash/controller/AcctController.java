package norman.trash.controller;

import norman.trash.domain.Acct;
import norman.trash.service.AcctService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
public class AcctController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctController.class);
    private static final String defaultSortColumn = "id";
    private static final String[] otherSortableColumns = {"name", "type"};
    @Autowired
    private AcctService service;

    @GetMapping("/acctList")
    public String loadList(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "sortColumn", required = false, defaultValue = "id") String sortColumn,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            Model model) {
        // Convert sort column from string to an array of strings.
        String[] sortColumns = {defaultSortColumn};
        if (Arrays.asList(otherSortableColumns).contains(sortColumn)) {
            sortColumns = new String[]{sortColumn, defaultSortColumn};
        }

        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortColumns);
        Page<Acct> page = service.findAll(pageable);
        AcctListForm listForm = new AcctListForm(page);
        model.addAttribute("listForm", listForm);
        return "acctList";
    }
}

package norman.trash;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @GetMapping("/")
    public String loadDashboard(Model model) {
        List<String> lines = new ArrayList<>();
        lines.add("This is a test.");
        lines.add("This is only a test.");
        model.addAttribute("lines", lines);
        return "index";
    }
}

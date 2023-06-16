package info.jab.ms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class IndexController {

    @GetMapping("/")
    public RedirectView handleFoo() {
        return new RedirectView("./actuator/userbeans");
    }
}

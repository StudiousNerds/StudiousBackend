package nerds.studiousTestProject.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/payment")
    public String test(){
        return "payment";
    }
}

package cn.wekyjay.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    // 匹配规则：不以 /api 开头，且不包含 "." (即不是静态资源文件) 的所有请求
    @GetMapping(value = {"/", "/{path:[^\\.]*}", "/{path:^(?!api).*$}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
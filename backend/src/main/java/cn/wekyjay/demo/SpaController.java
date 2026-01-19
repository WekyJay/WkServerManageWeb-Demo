package cn.wekyjay.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SpaController {
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 1. 如果是 API 请求，直接交给对应的 RestController 处理，不执行转发
        // 2. 如果是 index.html 本身，也不要转发，防止 StackOverflow
        if (uri.startsWith("/api") || uri.endsWith("/index.html")) {
            return null; // 返回 null 或抛出 404，让 Spring 继续查找其他匹配项
        }

        return "forward:/index.html";
    }
}
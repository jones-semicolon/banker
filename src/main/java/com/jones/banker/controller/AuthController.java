package com.jones.banker.controller;

import com.jones.banker.model.Customer;
import com.jones.banker.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final CustomerService service;
    public AuthController(CustomerService service) { this.service = service; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerForm(Model m) {
        m.addAttribute("customer", new Customer());
        return "register";
    }

    @GetMapping("/")
    public String dasboardPage(){
        return "redirect:/dashboard";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, Model model) {
        try {
            service.register(customer);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customer);
            return "register";
        }
    }
}

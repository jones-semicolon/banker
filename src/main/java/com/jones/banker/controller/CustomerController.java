package com.jones.banker.controller;

import com.jones.banker.model.Customer;
import com.jones.banker.model.Transaction;
import com.jones.banker.service.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String phone = auth.getName();
        Customer customer = service.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Reverse transactions in Java
        List<Transaction> reversed = new ArrayList<>(customer.getTransactions());
        Collections.reverse(reversed);

        model.addAttribute("customer", customer);
        model.addAttribute("transactions", reversed);

        return "dashboard";
    }

    @GetMapping("/withdraw")
    public String showWithdrawPage(Authentication auth, Model model) {
        String phone = auth.getName();
        Customer customer = service.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Reverse transactions in Java
        List<Transaction> reversed = new ArrayList<>(customer.getTransactions());
        Collections.reverse(reversed);

        model.addAttribute("customer", customer);
        model.addAttribute("transactions", reversed);
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam String accountId,
            @RequestParam double amount,
            Model model
    ) {
        try {
            service.withdraw(accountId, amount);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Also need customer in model
            Customer customer = service.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            model.addAttribute("customer", customer);
            return "withdraw";
        }
    }

    @GetMapping("/transfer")
    public String transferForm(Authentication auth, Model model) {
        String phone = auth.getName();
        Customer customer = service.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Reverse transactions in Java
        List<Transaction> reversed = new ArrayList<>(customer.getTransactions());
        Collections.reverse(reversed);

        model.addAttribute("customer", customer);
        model.addAttribute("transactions", reversed);
        return "transfer";
    }


    @PostMapping("/transfer")
    public String transfer(
            Authentication auth,
            @RequestParam String recipientPhone,
            @RequestParam double amount,
            Model m
    ) {
        String fromPhone = auth.getName();
        Customer sender = service.findByPhone(fromPhone)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        if (sender.balance < amount) {
            m.addAttribute("error", "Maximum amount exceeded");
            m.addAttribute("customer", sender);
            return "transfer";
        }

        // If recipient phone is same as sender's phone, reject
        if (recipientPhone.equals(fromPhone)) {
            m.addAttribute("error", "Cannot transfer to your own account");
            m.addAttribute("customer", sender);
            return "transfer";
        }

        Optional<Customer> maybeRecipient = service.findByPhone(recipientPhone);
        if (maybeRecipient.isEmpty()) {
            m.addAttribute("error", "Recipient not found");
            m.addAttribute("customer", sender);
            return "transfer";
        }
        Customer recipient = maybeRecipient.get();

        try {
            service.transfer(sender.getId(), recipient.getId(), amount);
            // m.addAttribute("success", True)
            return "transfer";
            // return "redirect:/dashboard";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            m.addAttribute("customer", sender);
            return "transfer";
        }
    }

    // Show deposit page
    @GetMapping("/deposit")
    public String showDepositPage(Authentication auth, Model model) {
        String phone = auth.getName();
        Customer customer = service.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Reverse transactions in Java
        List<Transaction> reversed = new ArrayList<>(customer.getTransactions());
        Collections.reverse(reversed);

        model.addAttribute("customer", customer);
        model.addAttribute("transactions", reversed);
        return "deposit";
    }

    // Handle deposit POST
    @PostMapping("/deposit")
    public String deposit(
            @RequestParam String accountId,
            @RequestParam double amount,
            Model model
    ) {
        try {
            service.deposit(accountId, amount);
            // return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Also need customer in model
            Customer customer = service.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            model.addAttribute("customer", customer);
            return "deposit";
        }
    }
}

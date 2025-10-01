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

    private void addCustomerAndTransactions(Customer customer, Model model, boolean success) {
        List<Transaction> reversed = new ArrayList<>(customer.getTransactions());
        Collections.reverse(reversed);
        model.addAttribute("customer", customer);
        model.addAttribute("transactions", reversed);
        model.addAttribute("success", success)
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        addCustomerAndTransactions(customer, model, false);
        return "dashboard";
    }

    @GetMapping("/withdraw")
    public String showWithdrawPage(Authentication auth, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        addCustomerAndTransactions(customer, model, false);
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(Authentication auth, @RequestParam double amount, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            service.withdraw(customer.getId(), amount);
            model.addAttribute("success", true)
            return "withdraw";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            addCustomerAndTransactions(customer, model, false);
            return "withdraw";
        }
    }

    @GetMapping("/transfer")
    public String transferForm(Authentication auth, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        addCustomerAndTransactions(customer, model, false);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(Authentication auth,
                           @RequestParam String recipientPhone,
                           @RequestParam double amount,
                           Model m) {
        Customer sender = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (amount > sender.getBalance()) {
            m.addAttribute("error", "Insufficient balance.");
            addCustomerAndTransactions(sender, m, false);
            return "transfer";
        }

        if (recipientPhone.equals(sender.getPhone())) {
            m.addAttribute("error", "Cannot transfer to your own account.");
            addCustomerAndTransactions(sender, m, false);
            return "transfer";
        }

        Optional<Customer> maybeRecipient = service.findByPhone(recipientPhone);
        if (maybeRecipient.isEmpty()) {
            m.addAttribute("error", "Recipient not found.");
            addCustomerAndTransactions(sender, m, false);
            return "transfer";
        }

        try {
            service.transfer(sender.getId(), maybeRecipient.get().getId(), amount);
            return "redirect:/dashboard";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            addCustomerAndTransactions(sender, m, false);
            return "transfer";
        }
    }

    @GetMapping("/deposit")
    public String showDepositPage(Authentication auth, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        addCustomerAndTransactions(customer, model, false);
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(Authentication auth, @RequestParam double amount, Model model) {
        Customer customer = service.findByPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            service.deposit(customer.getId(), amount);
            Customer updated = service.findById(customer.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

            addCustomerAndTransactions(updated, model, true);
            return "deposit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            addCustomerAndTransactions(customer, model, false);
            return "deposit";
        }
    }
}

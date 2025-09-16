package com.jones.banker.service;

import com.jones.banker.model.Customer;
import com.jones.banker.model.Transaction;
import com.jones.banker.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public List<Customer> all() {
        return repo.findAll();
    }

    public Optional<Customer> findById(String id) {
        return repo.findById(id);
    }

    public Optional<Customer> findByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    public Customer register(Customer c) {
        if (repo.existsByPhone(c.getPhone())) {
            throw new RuntimeException("Phone already used");
        }
        c.setPassword(passwordEncoder.encode(c.getPassword()));
        c.setBalance(0.0);
        return repo.save(c);
    }

    public Customer save(Customer c) {
        return repo.save(c);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    public Transaction deposit(String id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        Customer c = repo.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        c.setBalance(c.getBalance() + amount);
        Transaction t = new Transaction();
        t.setType("DEPOSIT");
        t.setFromAccountId(null);
        t.setToAccountId(c.getId());
        t.setAmount(amount);
        c.getTransactions().add(t);
        repo.save(c);
        return t;
    }

    public Transaction withdraw(String id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        Customer c = repo.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if (c.getBalance() < amount) throw new RuntimeException("Insufficient funds");
        c.setBalance(c.getBalance() - amount);
        Transaction t = new Transaction();
        t.setType("WITHDRAW");
        t.setFromAccountId(c.getId());
        t.setToAccountId(null);
        t.setAmount(amount);
        c.getTransactions().add(t);
        repo.save(c);
        return t;
    }

    public Transaction transfer(String fromId, String toId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        Customer from = repo.findById(fromId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Customer to = repo.findById(toId).orElseThrow(() -> new RuntimeException("Receiver not found"));
        if (from.getBalance() < amount) throw new RuntimeException("Insufficient funds");
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        Transaction t = new Transaction();
        t.setType("TRANSFER");
        t.setFromAccountId(fromId);
        t.setToAccountId(toId);
        t.setAmount(amount);

        from.getTransactions().add(t);
        to.getTransactions().add(t);

        // Save both documents
        repo.save(from);
        repo.save(to);

        return t;
    }
}

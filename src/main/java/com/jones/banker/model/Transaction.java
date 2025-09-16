package com.jones.banker.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id = UUID.randomUUID().toString();
    private String fromAccountId; // null for deposit
    private String toAccountId;   // null for withdraw
    private double amount;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String type; // "DEPOSIT","WITHDRAW","TRANSFER", "DISPUTE"


    public Transaction() {}

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }

    public String getToAccountId() { return toAccountId; }
    public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

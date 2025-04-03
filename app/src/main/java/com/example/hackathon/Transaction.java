
package com.example.hackathon;
public class Transaction {
    private String _id;
    private double amount;
    private String sender;
    private String receiver;
    private String description;
    private String date;

    // Default constructor (Required for Firebase)
    public Transaction() {}

    // Parameterized constructor
    public Transaction(String _id, double amount, String sender, String receiver, String description, String date) {
        this._id = _id;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.description = description;
        this.date = date;
    }

    // Getters
    public String getT_id() { return _id; }
    public double getAmount() { return amount; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}

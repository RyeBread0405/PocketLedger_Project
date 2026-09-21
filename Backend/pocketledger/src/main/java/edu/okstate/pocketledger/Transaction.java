package edu.okstate.pocketledger;

public class Transaction{
    private String date;
    private double amount;
    private String location;
    private String category;

    public Transaction(String date, double amount, String location, String category){
        this.date = date;
        this.amount = amount;
        this.location = location;
        this.category = category;
    }

    public String getDate(){
        return date;
    }

    public double getAmount(){
        return amount;
    }

    public String getLocation(){
        return location;
    }

    public String getCategory(){
        return category;
    }
}
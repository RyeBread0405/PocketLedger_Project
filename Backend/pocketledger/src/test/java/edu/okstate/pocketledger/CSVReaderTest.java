package edu.okstate.pocketledger;

import java.io.IOException;
import java.util.List;

public class CSVReaderTest{
    public static void main(String[] args) throws IOException{
        CSVReader reader = new CSVReader();

        String filePath = "C:\\Users\\rylee\\OneDrive\\Documents\\transaction.csv";

        List<Transaction> transactions = reader.readCSV(filePath);

        System.out.println("Number of transactions: " + transactions.size());

        for (Transaction transaction: transactions){
            System.out.println(
                transaction.getDate() + " | " + 
                transaction.getAmount() + " | " +
                transaction.getLocation() + " | " +
                transaction.getCategory()
            );
        }
    }
}
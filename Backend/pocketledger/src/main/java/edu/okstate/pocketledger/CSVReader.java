package edu.okstate.pocketledger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class CSVReader{

    public List<Transaction> readCSV(String filePath) throws IOException{
        //Define the file path
        Path path = Path.of(filePath);

        //Read the file
        List<String> lines = Files.readAllLines(path);

        //Create a list of transactions
        List<Transaction> transactions = new ArrayList<>();

        //Iterate through the list from the file
        for (int i = 1; i < lines.size(); ++i){
            //Define the line
            String line = lines.get(i);

            //Split the line
            String[] values = line.split(",");

            //Create a new transaction
            Transaction newTransaction = new Transaction(
                values[0],
                Double.parseDouble(values[1]),
                values[2],
                values[3]
            );

            //Add the transaction to the list
            transactions.add(newTransaction);
        }

        return transactions;
    }

}
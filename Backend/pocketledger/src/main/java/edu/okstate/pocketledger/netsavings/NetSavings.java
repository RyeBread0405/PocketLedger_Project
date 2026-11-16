package edu.okstate.pocketledger.netsavings;

public class NetSavings{
    private double TotalMonthlyBudgets;
    private double TotalMonthlyExpenses;
    private double NetSavingsAmount;

    public NetSavings(double totalMonthlyBudgets, double totalMonthlyExpenses) {
        this.TotalMonthlyBudgets = totalMonthlyBudgets;
        this.TotalMonthlyExpenses = totalMonthlyExpenses;
        this.NetSavingsAmount = totalMonthlyBudgets - totalMonthlyExpenses;
    }

    public double getTotalMonthlyBudgets() {
        return TotalMonthlyBudgets;
    }

    public double getTotalMonthlyExpenses() {
        return TotalMonthlyExpenses;
    }

    public double getNetSavingsAmount() {
        return NetSavingsAmount;
    }
}
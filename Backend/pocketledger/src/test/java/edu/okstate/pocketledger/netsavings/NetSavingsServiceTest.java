package edu.okstate.pocketledger.netsavings;

import edu.okstate.pocketledger.budget.BudgetResponse;
import edu.okstate.pocketledger.budget.BudgetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NetSavingsServiceTest {

    private BudgetService budgetService;
    private NetSavingsService netSavingsService;

    @BeforeEach
    void setUp() {
        budgetService = mock(BudgetService.class);
        netSavingsService = new NetSavingsService(budgetService);
    }

    // Test 1: Savings from multiple months are added together
    @Test
    void calculatesCumulativeSavings() {

        Integer userId = 1;

        YearMonth august = YearMonth.now().minusMonths(3);
        YearMonth september = YearMonth.now().minusMonths(2);

        when(budgetService.getBudgetMonths(userId))
            .thenReturn(List.of(
                august.atDay(1),
                september.atDay(1)
            ));

        when(budgetService.getMonthlyBudget(userId, august.toString()))
            .thenReturn(new BudgetResponse(
                august.toString(),
                new BigDecimal("1000"),
                new BigDecimal("800"),
                List.of()
            ));

        when(budgetService.getMonthlyBudget(userId, september.toString()))
            .thenReturn(new BudgetResponse(
                september.toString(),
                new BigDecimal("1200"),
                new BigDecimal("900"),
                List.of()
            ));

        BigDecimal result = netSavingsService.calculateNetSavings(userId);

        // August: 200, September: 300, Total: 500
        assertEquals(0, new BigDecimal("500").compareTo(result));
    }

    // Test 2: Overspending reduces total savings
    @Test
    void subtractsOverspendingFromSavings() {

        Integer userId = 1;

        YearMonth firstMonth = YearMonth.now().minusMonths(2);
        YearMonth secondMonth = YearMonth.now().minusMonths(1);

        when(budgetService.getBudgetMonths(userId))
            .thenReturn(List.of(
                firstMonth.atDay(1),
                secondMonth.atDay(1)
            ));

        when(budgetService.getMonthlyBudget(userId, firstMonth.toString()))
            .thenReturn(new BudgetResponse(
                firstMonth.toString(),
                new BigDecimal("1000"),
                new BigDecimal("800"),
                List.of()
            ));

        when(budgetService.getMonthlyBudget(userId, secondMonth.toString()))
            .thenReturn(new BudgetResponse(
                secondMonth.toString(),
                new BigDecimal("500"),
                new BigDecimal("600"),
                List.of()
            ));

        BigDecimal result = netSavingsService.calculateNetSavings(userId);

        // First month: +200, second month: -100
        assertEquals(0, new BigDecimal("100").compareTo(result));
    }

    // Test 3: Current month is not counted
    @Test
    void excludesCurrentMonth() {

        Integer userId = 1;

        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        YearMonth currentMonth = YearMonth.now();

        when(budgetService.getBudgetMonths(userId))
            .thenReturn(List.of(
                previousMonth.atDay(1),
                currentMonth.atDay(1)
            ));

        when(budgetService.getMonthlyBudget(userId, previousMonth.toString()))
            .thenReturn(new BudgetResponse(
                previousMonth.toString(),
                new BigDecimal("1000"),
                new BigDecimal("800"),
                List.of()
            ));

        BigDecimal result = netSavingsService.calculateNetSavings(userId);

        assertEquals(0, new BigDecimal("200").compareTo(result));

        // Verify the current month's budget was never retrieved
        verify(budgetService, never())
            .getMonthlyBudget(userId, currentMonth.toString());
    }

    // Test 4: Future months are not counted
    @Test
    void excludesFutureMonths() {

        Integer userId = 1;

        YearMonth futureMonth = YearMonth.now().plusMonths(1);

        when(budgetService.getBudgetMonths(userId))
            .thenReturn(List.of(futureMonth.atDay(1)));

        BigDecimal result = netSavingsService.calculateNetSavings(userId);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));

        verify(budgetService, never())
            .getMonthlyBudget(userId, futureMonth.toString());
    }

    // Test 5: No budgets means zero savings
    @Test
    void returnsZeroWhenNoBudgetsExist() {

        Integer userId = 1;

        when(budgetService.getBudgetMonths(userId))
            .thenReturn(List.of());

        BigDecimal result = netSavingsService.calculateNetSavings(userId);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));

        verify(budgetService, never())
            .getMonthlyBudget(anyInt(), anyString());
    }
}
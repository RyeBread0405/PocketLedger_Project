package edu.okstate.pocketledger.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Category, Integer> {
}
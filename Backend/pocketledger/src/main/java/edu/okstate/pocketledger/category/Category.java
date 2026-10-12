package edu.okstate.pocketledger.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// map row in categpry table to java object
// doesn't create table, just matches it (validate)
@Entity
@Table(name = "category")
public class Category {

    //Category Id (Primary key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_label", nullable = false, unique = true, length = 255)
    private String categoryLabel;

    // Empty constructor for JPA
    protected Category() {
    }

    // Constructor with parameters
    public Category(String categoryLabel){
        this.categoryLabel = categoryLabel;
    }

    public Integer getId(){
        return id;
    }

    public String getCategoryLabel(){
        return categoryLabel;
    }
}

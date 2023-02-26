package com.fayilmazdir91.loanapp.loanapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CreditResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private boolean isApproved;
    private double loanLimit;

    public CreditResult(boolean isApproved, double loanLimit) {
        this.isApproved = isApproved;
        this.loanLimit = loanLimit;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public double getLoanLimit() {
        return loanLimit;
    }
}
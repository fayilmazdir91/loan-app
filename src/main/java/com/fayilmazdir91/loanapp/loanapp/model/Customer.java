package com.fayilmazdir91.loanapp.loanapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String identityNumber;

    private String name;

    private String surname;

    private String phoneNumber;

    private LocalDate birthDate;

    private Double monthlyIncome;

    private Double collateral;

    @OneToOne(mappedBy = "customer")
    private CreditResult creditResult;

}
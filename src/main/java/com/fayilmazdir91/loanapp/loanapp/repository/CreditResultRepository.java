package com.fayilmazdir91.loanapp.loanapp.repository;

import com.fayilmazdir91.loanapp.loanapp.model.CreditResult;
import com.fayilmazdir91.loanapp.loanapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditResultRepository extends JpaRepository<CreditResult, Long> {

    CreditResult findByCustomer(Customer customer);
}

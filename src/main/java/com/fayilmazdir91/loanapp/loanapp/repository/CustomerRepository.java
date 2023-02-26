package com.fayilmazdir91.loanapp.loanapp.repository;

import com.fayilmazdir91.loanapp.loanapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findById(Long id);

    Customer findByIdentityNumberAndBirthDate(String identityNumber, LocalDate birthDate);
}

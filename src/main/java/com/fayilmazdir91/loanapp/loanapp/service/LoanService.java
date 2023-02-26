package com.fayilmazdir91.loanapp.loanapp.service;

import com.fayilmazdir91.loanapp.loanapp.exceptions.CustomerNotFoundException;
import com.fayilmazdir91.loanapp.loanapp.model.CreditResult;
import com.fayilmazdir91.loanapp.loanapp.model.Customer;
import com.fayilmazdir91.loanapp.loanapp.repository.CreditResultRepository;
import com.fayilmazdir91.loanapp.loanapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final CreditResultRepository creditResultRepository;

    public LoanService(CustomerRepository customerRepository, CreditResultRepository creditResultRepository) {
        this.customerRepository = customerRepository;
        this.creditResultRepository = creditResultRepository;
    }

    public int calculateCreditScore(String identityNumber) {
        return 750;
    }

    public CreditResult calculateCreditResult(Customer customer) {
        int creditScore = calculateCreditScore(customer.getIdentityNumber());
        boolean isApproved = false;
        double loanLimit = 0;


        if (creditScore>=500 && creditScore<1000 && customer.getMonthlyIncome()<5000) {
            isApproved = true;
            if(customer.getCollateral() > 0) {
                loanLimit = 10000 + customer.getCollateral()*((double)10/100);
            } else {
                loanLimit = 10000;
            }
        }

        if (creditScore>=500 && creditScore<1000 && customer.getMonthlyIncome()>=5000 && customer.getMonthlyIncome()<=10000) {
            isApproved = true;
            if(customer.getCollateral() > 0) {
                loanLimit = 20000 + customer.getCollateral()*((double)20/100);
            } else {
                loanLimit = 20000;
            }
        }

        if (creditScore>=500 && creditScore<1000 && customer.getMonthlyIncome()>10000) {
            isApproved = true;
            if(customer.getCollateral() > 0) {
                loanLimit = customer.getMonthlyIncome()*2 + customer.getCollateral()*((double)25/100);
            } else {
                loanLimit = customer.getMonthlyIncome()*2;
            }
        }

        if (creditScore>=1000) {
            isApproved = true;
            if(customer.getCollateral() > 0) {
                loanLimit = customer.getMonthlyIncome()*4 + customer.getCollateral()*((double)50/100);
            } else {
                loanLimit = customer.getMonthlyIncome()*4;
            }
        }


        CreditResult creditResult = new CreditResult(isApproved, loanLimit);
        creditResultRepository.save(creditResult);

        return creditResult;
    }

    public CreditResult getCreditResult(String identityNumber, LocalDate birthDate) {
        Customer customer = customerRepository.findByIdentityNumberAndBirthDate(identityNumber, birthDate);

        if(customer == null) {
            return null;
        }

        CreditResult creditResult = creditResultRepository.findByCustomer(customer);

        if(creditResult == null) {
            return null;
        }

        return creditResult;
    }

    public Customer getCustomerById(long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if(optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        } else {
            throw new CustomerNotFoundException("Customer with id " + id + "not found.");
        }
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

}

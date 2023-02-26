package com.fayilmazdir91.loanapp.loanapp.controller;

import com.fayilmazdir91.loanapp.loanapp.model.CreditResult;
import com.fayilmazdir91.loanapp.loanapp.model.Customer;
import com.fayilmazdir91.loanapp.loanapp.repository.CustomerRepository;
import com.fayilmazdir91.loanapp.loanapp.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    LoanService loanService;

    @Autowired
    private CustomerRepository customerRepository;

    public LoanController(LoanService loanService, CustomerRepository customerRepository) {
        this.loanService = loanService;
        this.customerRepository = customerRepository;
    }

    // GET CREDİT SCORE
    @GetMapping("/score/{identityNumber}")
    public ResponseEntity<Integer> getCreditScore(@PathVariable String identityNumber) {
        int score = loanService.calculateCreditScore(identityNumber);
        return ResponseEntity.ok(score);
    }

    // APPLY FOR LOAN
    @PostMapping("/apply")
    public ResponseEntity<String> applyLoan(@RequestBody Customer customer) {
        customerRepository.save(customer); // Persist the Customer entity

        CreditResult result = loanService.calculateCreditResult(customer);

        // SEND SMS TO CUSTOMER
        String message = "Your loan application has been " + (result.isApproved() ? "approved. " : "rejected. ") +
                "Your loan limit is " + result.getLoanLimit() + ".";
        sendSMS(customer.getPhoneNumber(), message);

        String res = result.isApproved() ? "approved" : "rejected";
        res += ", limit: " + result.getLoanLimit();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private void sendSMS(String phoneNumber, String message) {}

    @GetMapping("/credit-result")
    public ResponseEntity<CreditResult> getCreditApplication(@RequestParam("identityNumber") String identityNumber,
                                                             @RequestParam("birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate birthDate) {
        CreditResult result = loanService.getCreditResult(identityNumber, birthDate);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        CreditResult a = result.getCustomer().getCreditResult();
        return ResponseEntity.ok(a);
    }

    //  CRUD FOR CUSTOMER

    // GET ALL CUSTOMERS
    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    // GET CUSTOMER BY ID
    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable(value = "id") Long id) {
        Customer customer = loanService.getCustomerById(id);
        if(customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(customer);
    }

    // CREATE A CUSTOMER
    @PostMapping("/customers")
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        Customer addedCustomer = loanService.saveCustomer(customer);
        return ResponseEntity.ok().body(addedCustomer);
    }

    // UPDATE A CUSTOMER
    @PutMapping("/customer/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        Customer customer = loanService.getCustomerById(id);
        if (customer == null) {
           return ResponseEntity.notFound().build();
        }
        customer.setName(updatedCustomer.getName());
        customer.setSurname(updatedCustomer.getSurname());
        customer.setIdentityNumber(updatedCustomer.getIdentityNumber());
        customer.setBirthDate(updatedCustomer.getBirthDate());
        customer.setMonthlyIncome(updatedCustomer.getMonthlyIncome());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setCollateral(updatedCustomer.getCollateral());

        Customer savedCustomer = loanService.saveCustomer(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    // DELETE A CUSTOMER
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Object> deleteCustomerById(@PathVariable(value = "id") Long id) {
        Optional<Customer> customer = customerRepository.findById(id);

        if(customer.isPresent()) {
            customerRepository.deleteById(id);
            return new ResponseEntity<>("Customer is deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
    }
}

package com.fayilmazdir91.loanapp.loanapp.controller;

import com.fayilmazdir91.loanapp.loanapp.model.CreditResult;
import com.fayilmazdir91.loanapp.loanapp.model.Customer;
import com.fayilmazdir91.loanapp.loanapp.repository.CustomerRepository;
import com.fayilmazdir91.loanapp.loanapp.service.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private CustomerRepository customerRepository;

    private final LoanController loanController;

    public LoanControllerTest() {
        MockitoAnnotations.openMocks(this);
        this.loanController = new LoanController(loanService, customerRepository);
    }

    @Test
    void testGetCreditScore() {
        // given
        String identityNumber = "12345678901";
        int score = 700;
        when(loanService.calculateCreditScore(identityNumber)).thenReturn(score);

        // when
        ResponseEntity<Integer> response = loanController.getCreditScore(identityNumber);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(score, response.getBody());
    }

    @Test
    void testApplyLoan() {
        // given
        Customer customer = new Customer();
        CreditResult creditResult = new CreditResult(true, 10000.0);

        when(loanService.calculateCreditResult(customer)).thenReturn(creditResult);

        // when
        ResponseEntity<String> response = loanController.applyLoan(customer);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("approved, limit: 10000.0", response.getBody());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetCreditApplication() {
        // given
        String identityNumber = "12345678901";
        LocalDate birthDate = LocalDate.of(1990, 1,1);
        CreditResult creditResult = new CreditResult(true, 10000.0);
        Customer customer = new Customer();
        customer.setCreditResult(creditResult);
        when(loanService.getCreditResult(identityNumber, birthDate)).thenReturn(creditResult);
        creditResult.setCustomer(customer);

        // when
        ResponseEntity<CreditResult> response = loanController.getCreditApplication(identityNumber, birthDate);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(creditResult, response.getBody());
    }

    @Test
    void testGetCustomers() {
        // given
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customers.add(customer1);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customers.add(customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // when
        List<Customer> response = loanController.getCustomers();

        // then
        assertEquals(customers, response);
    }

    @Test
    void testGetCustomerById() {
        // given
        Long id = 1L;
        Customer customer = new Customer();
        customer.setId(id);
        when(loanService.getCustomerById(id)).thenReturn(customer);

        // when
        ResponseEntity<Customer> response = loanController.getCustomerById(id);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    void testSaveCustomer() {
        // given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setIdentityNumber("12345678901");
        customer.setBirthDate(LocalDate.of(1990,1,1));
        customer.setPhoneNumber("555-1234");

        when(loanService.saveCustomer(customer)).thenReturn(customer);

        // when
        ResponseEntity<Customer> response = loanController.saveCustomer(customer);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    void testUpdateCustomer() {
        // given
        Long id = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(id);
        existingCustomer.setName("John");
        existingCustomer.setSurname("Doe");
        existingCustomer.setIdentityNumber("12345678901");
        existingCustomer.setBirthDate(LocalDate.of(1990,1,1));
        existingCustomer.setMonthlyIncome(5000.0);
        existingCustomer.setPhoneNumber("555-555-5555");
        existingCustomer.setCollateral(3000.0);

        when(loanService.getCustomerById(id)).thenReturn(existingCustomer);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName("Martha");
        updatedCustomer.setSurname("Doe");
        updatedCustomer.setIdentityNumber("12345678901");
        updatedCustomer.setBirthDate(LocalDate.of(1990,1,1));
        updatedCustomer.setMonthlyIncome(5000.0);
        updatedCustomer.setPhoneNumber("555-555-5555");
        updatedCustomer.setCollateral(3000.0);

        when(loanService.saveCustomer(existingCustomer)).thenReturn(updatedCustomer);

        // when
        ResponseEntity<Customer> response = loanController.updateCustomer(id, updatedCustomer);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCustomer, response.getBody());
    }

    @Test
    void testDeleteCustomerById() {
        // given
        Long id = 1L;
        Optional<Customer> customer = Optional.of(new Customer());
        when(customerRepository.findById(id)).thenReturn(customer);

        // when
        ResponseEntity<Object> response = loanController.deleteCustomerById(id);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer is deleted successfully", response.getBody());
        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCustomerByIdNotFound() {
        // given
        Long id = 1L;
        Optional<Customer> customer = Optional.empty();
        when(customerRepository.findById(id)).thenReturn(customer);

        // when
        ResponseEntity<Object> response = loanController.deleteCustomerById(id);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
        verify(customerRepository, never()).deleteById(id);
    }


}
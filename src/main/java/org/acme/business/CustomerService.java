package org.acme.business;

import org.acme.domain.Customer;
import org.acme.dto.CustomerPageResponse;
import org.acme.dto.CustomerRequest;
import org.acme.dto.CustomerResponse;

import java.util.Optional;

public interface CustomerService {

    CustomerPageResponse getCustomers(int page, int limit);

    Optional<Customer> getCustomerById(Long id);

    CustomerResponse createCustomer(CustomerRequest customerDto);

    CustomerResponse updateCustomer(Long id, CustomerResponse customerDto);

    void deleteCustomer(Long id);
}

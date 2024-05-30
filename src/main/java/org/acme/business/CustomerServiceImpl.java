package org.acme.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.Customer;
import org.acme.dto.CustomerRequest;
import org.acme.dto.CustomerResponse;
import org.acme.persistance.CustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    @Inject
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findByIdOptional(id);
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerDto) {
        Customer savedCustomer = toCustomer(customerDto);
        customerRepository.persist(savedCustomer);
        return toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerResponse customerDto) {
        Customer entity = customerRepository.findByIdOptional(id).orElseThrow(NotFoundException::new);
        entity.setFirstName(customerDto.getFirstName());
        entity.setLastName(customerDto.getLastName());
        entity.setAge(customerDto.getAge());
        entity.setCity(customerDto.getCity());
        entity.setPostcode(customerDto.getPostcode());

        customerRepository.persist(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }


    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .age(customer.getAge())
                .city(customer.getCity())
                .postcode(customer.getPostcode())
                .build();
    }

    private Customer toCustomer(CustomerRequest customerDto) {
        return Customer.builder()
                .firstName(customerDto.getFirstName())
                .lastName(customerDto.getLastName())
                .age(customerDto.getAge())
                .city(customerDto.getCity())
                .postcode(customerDto.getPostcode())
                .build();
    }
}

package org.acme.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.Customer;
import org.acme.dto.CustomerDto;
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
    public List<CustomerDto> getCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findByIdOptional(id);
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer savedCustomer = toCustomer(customerDto);
        customerRepository.persist(savedCustomer);
        customerDto.setId(savedCustomer.getId());
        return customerDto;
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer entity = customerRepository.findByIdOptional(id).orElseThrow(NotFoundException::new);
        entity.setFirstName(customerDto.getFirstName());
        entity.setLastName(customerDto.getLastName());
        entity.setAge(customerDto.getAge());
        entity.setCity(customerDto.getCity());
        entity.setPostcode(customerDto.getPostcode());

        customerRepository.persist(entity);
        return toDto(entity);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }


    private CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .age(customer.getAge())
                .city(customer.getCity())
                .postcode(customer.getPostcode())
                .build();
    }

    private Customer toCustomer(CustomerDto customerDto) {
        return Customer.builder()
                .id(customerDto.getId())
                .firstName(customerDto.getFirstName())
                .lastName(customerDto.getLastName())
                .age(customerDto.getAge())
                .city(customerDto.getCity())
                .postcode(customerDto.getPostcode())
                .build();
    }
}

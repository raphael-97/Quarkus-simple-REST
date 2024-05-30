package org.acme.business;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.Customer;
import org.acme.dto.CustomerPageResponse;
import org.acme.dto.CustomerRequest;
import org.acme.dto.CustomerResponse;
import org.acme.persistance.CustomerRepository;

import java.util.Optional;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    @Inject
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerPageResponse getCustomers(int page, int limit) {
        PanacheQuery<Customer> allCustomers = customerRepository.findAll();

        allCustomers.page(Page.ofSize(limit));

        return CustomerPageResponse.builder()
                .total_pages(allCustomers.pageCount())
                .current_page(page)
                .next_page(page + 1 >= allCustomers.pageCount() ? null : page + 1)
                .prev_page(page <= 0 ? null : page - 1)
                .total_records((int)allCustomers.count())
                .records(allCustomers.page(Page.of(page, limit)).stream().map(this::toResponse).toList())
                .build();
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

package org.acme.persistance;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.Customer;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {

    public Customer findByFirstName(String firstName) {
        return find("firstName", firstName).firstResult();
    }

}

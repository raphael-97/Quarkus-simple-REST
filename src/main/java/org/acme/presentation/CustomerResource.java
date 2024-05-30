package org.acme.presentation;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NoArgsConstructor;
import org.acme.business.CustomerService;
import org.acme.domain.Customer;
import org.acme.dto.CustomerRequest;
import org.acme.dto.CustomerResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/customers")
@NoArgsConstructor
public class CustomerResource {

    private CustomerService customerService;

    @Inject
    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerResponse> getCustomers() {
        return customerService.getCustomers();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("id") Long id) {
        Optional<Customer> customerById = customerService.getCustomerById(id);

        if(customerById.isPresent()) {
            return Response.ok(customerById.get()).build();
        }
        return Response.noContent().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(@RequestBody CustomerRequest customerDto) {
        CustomerResponse customerDtoResponse = customerService.createCustomer(customerDto);
        return Response.created(URI.create("/customers/" + customerDtoResponse.getId()))
                .entity(customerDtoResponse).build();
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") Long id,@RequestBody CustomerResponse customerDto) {
        CustomerResponse customerDtoResponse = customerService.updateCustomer(id, customerDto);
        return Response.ok(customerDtoResponse).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.ok().build();
    }
}

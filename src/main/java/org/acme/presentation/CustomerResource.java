package org.acme.presentation;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NoArgsConstructor;
import org.acme.business.CustomerService;
import org.acme.domain.Customer;
import org.acme.dto.CustomerPageResponse;
import org.acme.dto.CustomerRequest;
import org.acme.dto.CustomerResponse;
import org.acme.exceptions.ErrorMessage;
import org.acme.exceptions.WrongInputException;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/customers")
@NoArgsConstructor
public class CustomerResource {

    private CustomerService customerService;

    private List<String> customerFields;


    @Inject
    public CustomerResource(CustomerService customerService) {
        this.customerService = customerService;
        customerFields = Arrays.stream(Customer.class.getDeclaredFields())
                .map(Field::getName)
                .filter(elem -> elem.charAt(0) != '$')
                .collect(Collectors.toList());
    }

    @ServerExceptionMapper
    public Response mapException(WrongInputException e) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(e.getMessage())
                .status(e.getStatus())
                .build();
        return Response.status(errorMessage.getStatus()).entity(errorMessage).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@QueryParam("page") int page,
                                 @QueryParam("limit") int limit,
                                 @QueryParam("sort_by") @DefaultValue("id") String sortBy,
                                 @QueryParam("sort_order") @DefaultValue("asc") String sortOrder) throws WrongInputException {

        if(page < 0)
            throw new WrongInputException("QueryParam page can not be smaller than 0",
                    Response.Status.BAD_REQUEST.getStatusCode());
        if(limit <= 0)
            throw new WrongInputException("QueryParam limit can not be smaller than 1",
                    Response.Status.BAD_REQUEST.getStatusCode());

        if(!customerFields.contains(sortBy))
            throw new WrongInputException("QueryParam sort_by only accepts: " + String.join(", ", customerFields),
                    Response.Status.BAD_REQUEST.getStatusCode());

        if(!sortOrder.equals("asc") && !sortOrder.equals("desc"))
            throw new WrongInputException("QueryParam sort_order has to be asc or desc",
                    Response.Status.BAD_REQUEST.getStatusCode());

        CustomerPageResponse customers = customerService.getCustomers(page, limit, sortBy, sortOrder);
        return Response.ok(customers).build();
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

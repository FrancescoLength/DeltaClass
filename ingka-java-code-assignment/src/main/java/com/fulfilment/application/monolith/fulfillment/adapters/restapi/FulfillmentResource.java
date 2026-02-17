package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateProductOperation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("fulfillment")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FulfillmentResource {

    private final AssociateProductOperation associateProductOperation;

    @Inject
    public FulfillmentResource(AssociateProductOperation associateProductOperation) {
        this.associateProductOperation = associateProductOperation;
    }

    @POST
    @Transactional
    public Response associate(Fulfillment fulfillment) {
        associateProductOperation.associate(fulfillment);
        return Response.ok(fulfillment).status(201).build();
    }
}

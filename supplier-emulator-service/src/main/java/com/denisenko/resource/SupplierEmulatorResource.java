package com.denisenko.resource;

import com.denisenko.model.SupplierOrder;
import com.denisenko.model.SupplyRequest;
import com.denisenko.model.SupplyResponse;
import com.denisenko.service.SupplyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/supply")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SupplierEmulatorResource {
    private static final Logger log = Logger.getLogger(SupplierEmulatorResource.class);
    private final Map<String, SupplierOrder> orders = new HashMap<>();

    @Inject
    SupplyService supplyService;

    @POST
    public Response placeOrder(SupplierOrder order) {
        log.info("Order was received: " + order);
        String supplyId = UUID.randomUUID().toString();
        orders.put(supplyId, order);
        return Response.ok(supplyId).build();
    }

    @POST
    @Path("/send")
    public Response sendSupply(SupplyRequest request) {
        log.info("Prepare to send supply with ID = " + request.supplyId());
        SupplierOrder order = orders.get(request.supplyId());
        if (order == null) {
            log.warn("Order with supply ID = " + request.supplyId() + " doesn't exist");
            return Response.status(NOT_FOUND).build();
        }

        SupplyResponse supplyResponse = supplyService.sendSupply(order);
        return Response.ok(supplyResponse).build();
    }
}

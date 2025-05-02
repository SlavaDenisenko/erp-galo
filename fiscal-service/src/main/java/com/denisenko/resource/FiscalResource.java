package com.denisenko.resource;

import com.denisenko.exception.OrderOperationException;
import com.denisenko.exception.ShiftOperationException;
import com.denisenko.model.Order;
import com.denisenko.service.FiscalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/fiscal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FiscalResource {

    @Inject
    FiscalService fiscalService;

    @POST
    @Path("/shift/open")
    public Response openShift(@HeaderParam("X-User") String cashierName,
                              @HeaderParam("Idempotency-Key") String idempotencyKey) {
        try {
            return Response.ok(fiscalService.openShift(cashierName, idempotencyKey)).build();
        } catch (ShiftOperationException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/shift/close")
    public Response closeShift(@HeaderParam("X-User") String cashierName,
                               @HeaderParam("Idempotency-Key") String idempotencyKey) {
        try {
            return Response.ok(fiscalService.closeShift(cashierName, idempotencyKey)).build();
        } catch (ShiftOperationException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/order/close")
    public Response closeOrder(Order order, @HeaderParam("Idempotency-Key") String idempotencyKey) {
        try {
            return Response.ok(fiscalService.closeOrder(order, idempotencyKey)).build();
        } catch (OrderOperationException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}

package com.denisenko.resource;

import com.denisenko.model.OFDResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/ofd")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OFDEmulatorResource {
    private final AtomicInteger requestCount = new AtomicInteger(0);

    @POST
    @Path("/shift/open")
    public Response openShift(Map<String, Object> shift) {
        Response errorResponse = validateOpenedShift(shift);
        if (Objects.nonNull(errorResponse)) return errorResponse;
        if (shouldReturnError()) return createErrorResponse();
        UUID shiftNumber = UUID.randomUUID();
        return createSuccessResponse(shiftNumber.toString());
    }

    @POST
    @Path("/shift/close")
    public Response closeShift(Map<String, Object> shift) {
        Response errorResponse = validateClosedShift(shift);
        if (Objects.nonNull(errorResponse)) return errorResponse;
        if (shouldReturnError()) return createErrorResponse();
        UUID shiftNumber = UUID.randomUUID();
        return createSuccessResponse(shiftNumber.toString());
    }

    @POST
    @Path("/order/close")
    public Response closeOrder(Map<String, Object> order) {
        Response errorResponse = validateOrder(order);
        if (Objects.nonNull(errorResponse)) return errorResponse;
        if (shouldReturnError()) return createErrorResponse();
        UUID shiftNumber = UUID.randomUUID();
        return createSuccessResponse(shiftNumber.toString());
    }

    private Response validateOpenedShift(Map<String, Object> shift) {
        String cashierName = (String) shift.get("openedBy");
        return (Objects.isNull(cashierName) || cashierName.isBlank()) ? createBadRequestResponse("Cashier name is required") : null;
    }

    private Response validateClosedShift(Map<String, Object> shift) {
        String cashierName = (String) shift.get("closedBy");
        return (Objects.isNull(cashierName) || cashierName.isBlank()) ? createBadRequestResponse("Cashier name is required") : null;
    }

    private Response validateOrder(Map<String, Object> order) {
        String paymentMethod = (String) order.get("paymentMethod");
        return (Objects.isNull(paymentMethod) || paymentMethod.isBlank()) ? createBadRequestResponse("Order ID is required") : null;
    }

    private boolean shouldReturnError() {
        return requestCount.incrementAndGet() % 5 == 0;
    }

    private Response createSuccessResponse(String shiftNumber) {
        OFDResponse response = new OFDResponse();
        response.setStatus("SUCCESS");
        response.setOfdNumber(shiftNumber);
        response.setTimestamp(LocalDateTime.now());
        return Response.ok(response).build();
    }

    private Response createBadRequestResponse(String errorMessage) {
        OFDResponse response = new OFDResponse();
        response.setStatus("ERROR");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorMessage(errorMessage);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }

    private Response createErrorResponse() {
        OFDResponse response = new OFDResponse();
        response.setStatus("ERROR");
        response.setTimestamp(LocalDateTime.now());
        response.setErrorMessage("Random server error occurred");
        return Response.serverError()
                .entity(response)
                .build();
    }
}

package com.denisenko.resource;

import com.denisenko.model.Order;
import com.denisenko.model.Shift;
import com.denisenko.service.PrintService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/print")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public class PrintResource {

    @Inject
    PrintService printService;

    @POST
    @Path("/order")
    public Response printOrder(Order order) {
        String printerResponse = printService.printOrder(order);
        return printerResponse.isEmpty() ?
                Response.ok().build() :
                Response.serverError().entity(printerResponse).build();
    }

    @POST
    @Path("/check")
    public Response printCheck(Order order) {
        String printerResponse = printService.printCheck(order);
        return printerResponse.isEmpty() ?
                Response.ok().build() :
                Response.serverError().entity(printerResponse).build();
    }

    @POST
    @Path("/fiscal/shift")
    public Response printFiscalShift(Shift shift) {
        String printerResponse = printService.printFiscalShift(shift);
        return printerResponse.isEmpty() ?
                Response.ok().build() :
                Response.serverError().entity(printerResponse).build();
    }

    @POST
    @Path("/fiscal/order")
    public Response printFiscalOrder(Order order) {
        String printerResponse = printService.printFiscalOrder(order);
        return printerResponse.isEmpty() ?
                Response.ok().build() :
                Response.serverError().entity(printerResponse).build();
    }
}

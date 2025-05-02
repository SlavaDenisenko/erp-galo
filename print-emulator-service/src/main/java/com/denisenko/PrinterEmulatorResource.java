package com.denisenko;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Path("/printer")
@Consumes(MediaType.TEXT_HTML)
@Produces(MediaType.TEXT_PLAIN)
public class PrinterEmulatorResource {
    private static final Logger log = LoggerFactory.getLogger(PrinterEmulatorResource.class);
    private static final Random random = new Random();

    @POST
    @Path("/print")
    public Response print(String receipt) {
        if (random.nextInt(100) < 20) {
            log.error("Error occurred while printing due to a lost connection");
            return Response.serverError().entity("Impossible to print because there is no connection").build();
        }

        log.info("Printing receipt:\n{}", receipt);
        return Response.ok().build();
    }
}

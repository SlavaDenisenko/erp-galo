package com.denisenko.resource;

import com.denisenko.model.Report;
import com.denisenko.service.ReportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.Objects;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    ReportService reportService;

    @GET
    public Response getAvailableReports() {
        return Response.ok(reportService.getAvailableReports()).build();
    }

    @GET
    @Path("/{reportName}")
    public Response getReport(@PathParam("reportName") String reportName) {
        Report report = reportService.getReport(reportName);
        if (Objects.isNull(report))
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Report not found or data unavailable: " + reportName))
                    .build();

        return Response.ok(report).build();
    }
}

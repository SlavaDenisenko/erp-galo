package com.denisenko.resource;

import com.denisenko.dto.AuthResponse;
import com.denisenko.dto.RoleResponse;
import com.denisenko.dto.UserCredentials;
import com.denisenko.service.AccessControlService;
import com.denisenko.service.AuthService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/auth")
public class AuthResource {
    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @Inject
    AccessControlService accessControlService;

    @POST
    @Path("/login")
    public Response login(UserCredentials userCredentials) {
        try {
            AuthResponse authResponse = authService.login(userCredentials);
            return Response.ok(authResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid username or password").build();
        }
    }

    @GET
    @Path("/verify")
    @Authenticated
    public Response verifyToken(@Context SecurityContext securityContext,
                                @HeaderParam("X-Auth-Request-Redirect") String uri,
                                @HeaderParam("X-Original-Method") String method) {
        try {
            JsonWebToken jwt = (JsonWebToken) securityContext.getUserPrincipal();
            RoleResponse roleResponse = authService.getRole(jwt);
            String fullname = jwt.getClaim("name");
            if (accessControlService.isAllowed(uri, method, roleResponse.role())) {
                return Response.ok()
                        .header("X-User", fullname)
                        .header("X-User-Role", roleResponse.role())
                        .build();
            }
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error while verifying token", e);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}

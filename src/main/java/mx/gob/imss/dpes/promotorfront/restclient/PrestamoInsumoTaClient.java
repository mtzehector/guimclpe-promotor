package mx.gob.imss.dpes.promotorfront.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/prestamoInsumosTa")
@RegisterRestClient
public interface PrestamoInsumoTaClient {

    @GET
    @Path("/{idSolicitud}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response obtenerPrestamoInsumoPorCveSolicitud(@PathParam("idSolicitud") Long cveSolicitud);
}

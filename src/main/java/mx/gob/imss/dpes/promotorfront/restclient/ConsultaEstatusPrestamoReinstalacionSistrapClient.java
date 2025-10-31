package mx.gob.imss.dpes.promotorfront.restclient;

import mx.gob.imss.dpes.interfaces.sipre.model.ConsultaEstatusRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/reinstalacionPrestamo")
@RegisterRestClient
public interface ConsultaEstatusPrestamoReinstalacionSistrapClient {

    @POST
    @Path("/consultaEstatusPrestamoReinstalacion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response obtenerEstatus(ConsultaEstatusRequest request);
    
}

package mx.gob.imss.dpes.promotorfront.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.promotorfront.model.PersonaModel;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente Persona - Pensionado
 * @author luisr.rodriguez
 */
@Path("/pensionado")
@RegisterRestClient
public interface PensionadoClient {
    
    @PUT
    @Path("/actualizarNombre")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateDatos(PersonaModel persona);
    
}

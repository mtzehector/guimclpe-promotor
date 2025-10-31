/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.interfaces.pensionado.model.Pensionado;
import mx.gob.imss.dpes.promotorfront.model.BajaPromotorRq;
import mx.gob.imss.dpes.promotorfront.model.PersonaRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author edgar.arenas
 */
@Path("/persona")
@RegisterRestClient
public interface PersonaFrontClient {

    @GET
    @Path("/{curp}/{cveEF}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response load(@PathParam("curp") String curp, @PathParam("cveEF") Long cveEF);
    
    @GET
    @Path("/{curp}/{cveEF}/excel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response loadExcel(@PathParam("curp") String curp, @PathParam("cveEF") Long cveEF);
    
    @POST
  @Path("/bajaPromotor")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response bajaPromotor(BajaPromotorRq request);

}

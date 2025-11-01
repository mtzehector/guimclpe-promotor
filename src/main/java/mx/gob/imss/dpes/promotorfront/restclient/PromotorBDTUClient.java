/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author osiris.hernandez
 */
@Path("/promotor")
@RegisterRestClient
public interface PromotorBDTUClient {

    @POST
    @Path("/obtenerPorCurp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response obtenerPorCurp(RequestPromotorModel request);
    
    @POST
    @Path("/obtenerPorCurpNSS")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response obtenerPorCurpNSS(RequestPromotorModel request);
}

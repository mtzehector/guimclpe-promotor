/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.endpoint;

import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.common.endpoint.BaseGUIEndPoint;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import mx.gob.imss.dpes.promotorfront.service.OperadorService;
import mx.gob.imss.dpes.promotorfront.service.PromotorPorCvePersonalEFService;
import mx.gob.imss.dpes.promotorfront.service.PromotorService;
import mx.gob.imss.dpes.promotorfront.service.RelacionLaboralService;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 *
 * @author osiris.hernandez
 */
@Path("/promotor")
@RequestScoped
public class PromotorEndPoint extends BaseGUIEndPoint<RequestPromotorModel, RequestPromotorModel, RequestPromotorModel> {

    @Inject
    PromotorService promotorService;

    @Inject
    PromotorPorCvePersonalEFService promotorPorCvePersonalEFService;

    @Inject
    OperadorService operadorService;
    
    @Inject
    RelacionLaboralService relacionLaboralService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene los datos de un promotor",
            description = "Obtiene los datos de un promotor")
    public Response busquedaPromotor(RequestPromotorModel request) throws BusinessException {
        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotor request= {0}", request);

        if(!(request != null && request.getCurp() != null && !request.getCurp().trim().isEmpty() &&
                request.getNss() != null && !request.getNss().trim().isEmpty()
        ))
            return Response.status(Response.Status.BAD_REQUEST).build();

        ServiceDefinition[] steps = {promotorService};
        try {
            Message<RequestPromotorModel> response = promotorService.executeSteps(steps, new Message<>(request));
            //log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotor FINALIZA busquedaPromotor request= {0}", request);
            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR promotorFront PromotorEndPoint.busquedaPromotor ", e);
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new PromotorEndPointExeption(), null));
        }
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/valida")
    @Operation(summary = "Valida la relacion laboral del promotor",
            description = "Valida la relacion laboral del promotor")
    public Response validaPromotor(RequestPromotorModel request) throws BusinessException {
        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.validaPromotor request= {0}", request);
        List<String> response = relacionLaboralService.validaRelacion(new Message<>(request));
        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.validaPromotor FINALIZA validaPromotor request= {0}", request);
        return Response.ok(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/operador")
    @Operation(summary = "Obtiene los datos de un operador",
            description = "Obtiene los datos de un operador")
    public Response busquedaOperador(RequestPromotorModel request) {

        if(!(request != null && request.getCurp() != null && !request.getCurp().trim().isEmpty()))
            return Response.status(Response.Status.BAD_REQUEST).build();

        ServiceDefinition[] steps = {operadorService};
        try {
            Message<RequestPromotorModel> response = operadorService.executeSteps(steps, new Message<>(request));
            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorEndPoint.busquedaOperador ", e);
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                    new PromotorEndPointExeption(),
                    null));
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene los datos de un promotor",
            description = "Obtiene los datos de un promotor")
    public Response busquedaPromotorPorIdPersonalEF(@PathParam("id") Long id) {
        try {
            //log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotorPorIdPersonalEF path param : {0}", id);

            ServiceDefinition[] steps = {promotorPorCvePersonalEFService};

            RequestPromotorModel rpm = new RequestPromotorModel();
            rpm.setId(id);

            Message<RequestPromotorModel> request = new Message<>();
            request.setPayload(rpm);
            //log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotorPorIdPersonalEF request = \n {0}", request);

            Message<RequestPromotorModel> response = promotorPorCvePersonalEFService.executeSteps(steps, request);

            //if (Message.isException(response))
                //log.log(Level.INFO, "Response cach: {0}", response);
                //return toResponse(response);

            //log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotor FINALIZA busquedaPromotor request= {0}", request);
            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorEndPoint.busquedaPromotorPorIdPersonalEF {0}", e);
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                    new PromotorEndPointExeption(), null));
        }
    }
    
    @GET
    @Path("/{id}/noimage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene los datos de un promotor",
            description = "Obtiene los datos de un promotor")
    public Response busquedaPromotorPorIdPersonalEFNoImage(@PathParam("id") Long id) throws BusinessException {

        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotorPorIdPersonalEF path param : {0}", id);

        ServiceDefinition[] steps = {promotorPorCvePersonalEFService};

        Message<RequestPromotorModel> request = new Message<>();
        RequestPromotorModel rpm = new RequestPromotorModel();
        rpm.setId(id);
        request.setPayload(rpm);
        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotorPorIdPersonalEF request = \n {0}", request);

        Message<RequestPromotorModel> response = promotorPorCvePersonalEFService.executeSteps(steps, request);

        if (Message.isException(response)) {
            log.log(Level.INFO, "Response cach: {0}", response);
            return toResponse(response);
        }
        log.log(Level.INFO, ">>>>> promotorFront PromotorEndPoint.busquedaPromotor FINALIZA busquedaPromotor request= {0}", request);
        return toResponse(response);
    }

}

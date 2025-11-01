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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import mx.gob.imss.dpes.common.endpoint.BaseGUIEndPoint;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.exception.PartialContentFlowException;
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFException;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import mx.gob.imss.dpes.promotorfront.service.*;

/**
 *
 * @author edgar.arenas
 */
@Path("/promotor/prestamo")
@RequestScoped
public class PromotorPrestamoEndPoint extends BaseGUIEndPoint<PromotorPrestamoModel, PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    RenapoService renapo;
    @Inject
    BDTUService bdtu;
    @Inject
    PensionadoSistrap pensionadoSistrap;
    @Inject
    PensionSistrap pensionSistrap;
    @Inject
    SolicitudesVigentesService solicitudes;
    @Inject
    CapacidadCreditoSistrap capacidadSistrap;
    @Inject
    PersonaService persona;
    @Inject
    PensionadoService persisitirPensionado;
    @Inject
    SolicitudService crearSolicitud;
    @Inject
    CorreoService correo;
    @Inject
    CondicionesPrestamoService condicionesPrestamo;
    @Inject
    ControlService crearControl;
    @Inject
    FolioService crearfolio;
    @Inject
    PrestamoService prestamo;
    @Inject
    EnvioTokenService enviarToken;
    @Inject
    AmortizacionDatosBaseService amortizacionBaseService;
    @Inject
    PrestamoRecuperacionService prestamoRecuperacionService;
    @Inject
    CronJobService cronJobService;
    @Inject
    EntidadFinancieraService entidadFinancieraService;
    @Inject
    AmortizacionAutoService amortizacionAutoService;
    @Inject
    ConsultarEntidadFinancieraLogoService ceflogo;
    @Inject
    PromotorPorCvePersonalEFService promotorPorCvePersonalEFService;

    @Inject
    SolicitudHabilitarService solicitudHS;
    
    @Inject
    CorreoMontoLiquidarService correoMontoLiquidarService;

    @Inject
    ConsultaEstatusPrestamoReinstalacionSistrap consultaEstatusPrestamoReinstalacionSistrap;

    @Inject
    ConsultaPrestamoDescuentoNoAplicadoSistrap consultaPrestamoDescuentoNoAplicadoSistrap;

    @Inject
    SolicitudReinstalacionService solicitudReinstalacionService;

    @Inject
    PrestamoReinstalarService prestamoReinstalarService;

    @Inject
    PrestamoRecuperacionReinstalacionService prestamoRecuperacionReinstalacionService;

    @Inject
    CancelaSolicitudService cancelaSolicitudService;

    @Inject
    ValidaTablaAmortizacionService validaTablaAmortizacionService;

    @Inject
    CondicionesPrestamoReinstalacionService condicionesPrestamoReinstalacionService;
            
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validar/pensionado")
    public Response validarPrestamoPensionado(@Context HttpHeaders headers, PromotorPrestamoModel request) {

        try {
            String tokenSeguridad = this.obtenerTokenSeguridad(headers.getRequestHeaders());
            request.setToken(tokenSeguridad);

            ServiceDefinition[] steps = {
                renapo,
                pensionSistrap,
                pensionadoSistrap,
                capacidadSistrap,
                solicitudes,
                persona
            };

            Message<PromotorPrestamoModel> response = renapo.executeSteps(steps, new Message<>(request));

            return toResponse(response);
        } catch (VariableMessageException e) {
            return toResponse(new Message(null, ServiceStatusEnum.PARTIAL_CONTENT,
                new PartialContentFlowException(e.getMessage()), null));
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.validarPrestamoPensionado request = [" +
                request + "]", e);

            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new PromotorEndPointExeption(),null));
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registrar/prestamo")
    public Response registrarPrestamo(PromotorPrestamoModel request) throws BusinessException {

        log.log(Level.INFO, ">>>promotorFront|PromotorPrestamoEndPoint|registrarPrestamo {0}", request);
        ServiceDefinition[] steps = {
            enviarToken,
            persisitirPensionado,
            crearSolicitud,
            crearControl,
            crearfolio,
            prestamo,
            prestamoRecuperacionService,
            correoMontoLiquidarService,
            amortizacionBaseService,
            cronJobService,
            correo,
            ceflogo,
            solicitudHS};
        Message<PromotorPrestamoModel> response = enviarToken.executeSteps(steps, new Message<>(request));
       
        if (Message.isException(response)) {
            log.log(Level.INFO, "Response catch: {0}", response);
            return toResponse(response);
        }
        
        log.log(Level.INFO, "Consultar Imagen Promotor : {0}", request.getPersonaEF());

        ServiceDefinition[] steps2 = {promotorPorCvePersonalEFService};

        Message<RequestPromotorModel> request2 = new Message<>();
        RequestPromotorModel rpm = new RequestPromotorModel();
        rpm.setId(request.getPersonaEF().getIdPersonaEF());
        request2.setPayload(rpm);
        Message<RequestPromotorModel> response2 = promotorPorCvePersonalEFService.executeSteps(steps2, request2);
        
        log.log(Level.INFO, "Respuesta Imagen Promotor {0}", response2);
        
        response.getPayload().setPromotor(response2.getPayload());
        
        if (Message.isException(response2)) {
            log.log(Level.INFO, "Response catch: {0}", response);
            return toResponse(response);
        }
        return toResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registrar/amortauto")
    public Response registrarPrestamoAuto(AmortizacionInsumos source) throws BusinessException {
        int test = 0;
        if (source != null && source.getTest() != null && source.getTest() == 1) {
            test = 1;
        }
        List<AmortizacionInsumos> amortizacionInsumosList = amortizacionAutoService.execute(test);
        return Response.ok(amortizacionInsumosList).build();
    }

    @POST
    @Path("/validarentidadfinanciera")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarEF(PromotorPrestamoModel request) throws BusinessException {
        ServiceDefinition[] step = {entidadFinancieraService};
        Message<PromotorPrestamoModel> response = entidadFinancieraService.executeSteps(step, new Message<>(request));
        return Response.ok(response.getPayload().getEntidadFinanciera()).build();
    }
    
    @POST
    @Path("/validarCondiciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarMontos(PromotorPrestamoModel request) {
        try {
            ServiceDefinition[] step = {condicionesPrestamo};
            Message<PromotorPrestamoModel> response = condicionesPrestamo.executeSteps(step, new Message<>(request));
            if (Message.isException(response))
                return toResponse(response);

            return Response.ok(response.getPayload().getPersonaRequest().getPrestamo()).build();
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.validarMontos - request = [" + request + "] ",
                e);
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new CondicionesEFException(), null));
        }
    }
    
    @POST
    @Path("/confirmarCondiciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmarCondiciones(@Context HttpHeaders headers, PromotorPrestamoModel request) throws BusinessException {
        log.log(Level.INFO, ">>>promotorFront|PromotorPrestamoEndPoint|confirmarCondiciones {0}", request);

        String tokenSeguridad = this.obtenerTokenSeguridad(headers.getRequestHeaders());
        request.setToken(tokenSeguridad);
        
        ServiceDefinition[] step1 = {capacidadSistrap};    
        Message<PromotorPrestamoModel> response1 = capacidadSistrap.executeSteps(step1, new Message<>(request));
        if (Message.isException(response1)) {
            log.log(Level.INFO, "Response catch: {0}", response1);
            return toResponse(response1);
        }
        Double capacidadReal = response1.getPayload().getCapacidad().getImpCapacidadTotal();
        if(request.getPrestamosRecuperacionArreglo() != null && !request.getPrestamosRecuperacionArreglo().isEmpty()){
            switch(request.getPrestamo().getTipoCredito().getId().intValue()){
                case 2:
                case 3:
                case 4:
                    for(PrestamoRecuperacion p : request.getPrestamosRecuperacionArreglo()){
                       capacidadReal += p.getCanDescuentoMensual();
                    }
                    log.log(Level.INFO, ">>>Capacidad con prestamos vigentes: {0}", capacidadReal);
                    response1.getPayload().getCapacidad().setImpCapacidadTotal(capacidadReal);
                    break;
                case 1:
                    throw new BusinessException();
            }
        }
         
         
        ServiceDefinition[] step2 = {condicionesPrestamo};
        Message<PromotorPrestamoModel> response2 = condicionesPrestamo.executeSteps(step2, new Message<>(request));
        if (Message.isException(response2)) {
            log.log(Level.INFO, "Response catch: {0}", response2);
            return toResponse(response2);
        }
//        Double montoTotalNew = Math.round(Double.parseDouble(response2.getPayload().getPersonaRequest().getPrestamo().getMontoPagar()) * 100d) / 100d;
//        log.log(Level.INFO, ">>>Montos: {0}", montoTotalPrestamo + " -- " + montoTotalNew.toString());
//        if(!montoTotalPrestamo.equals(montoTotalNew.toString())){
//          throw new BusinessException();  
//        }
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validar/reinstalacion")
    public Response validarPrestamoReinstalacion(@Context HttpHeaders headers, PromotorPrestamoModel request) {

        try {
            String tokenSeguridad = this.obtenerTokenSeguridad(headers.getRequestHeaders());
            request.setToken(tokenSeguridad);

            ServiceDefinition[] steps = {
                renapo,
                pensionSistrap,
                pensionadoSistrap,
                capacidadSistrap,
                solicitudes,
                persona,
                consultaEstatusPrestamoReinstalacionSistrap,
                consultaPrestamoDescuentoNoAplicadoSistrap
            };

            Message<PromotorPrestamoModel> response = renapo.executeSteps(steps, new Message<>(request));

            return toResponse(response);
        } catch (VariableMessageException e) {
            return toResponse(new Message(null, ServiceStatusEnum.PARTIAL_CONTENT,
                new PartialContentFlowException(e.getMessage()), null));
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.validarPrestamoReinstalacion request = [" +
                    request + "]", e);

            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new PromotorEndPointExeption(),null));
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registrar/reinstalacion")
    public Response registrarReinstalacion(PromotorPrestamoModel request) {

        try {
            ServiceDefinition[] steps = {
                solicitudReinstalacionService,
                crearControl,
                crearfolio,
                prestamoReinstalarService,
                prestamoRecuperacionReinstalacionService,
                amortizacionBaseService,
                validaTablaAmortizacionService,
                cancelaSolicitudService,
                correo,
                ceflogo };

            Message<PromotorPrestamoModel> response =
                solicitudReinstalacionService.executeSteps(steps, new Message<>(request));

            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.registrarReinstalacion request = [" +
                request + "]", e);

            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new PromotorEndPointExeption(),null));
        }
    }

    @POST
    @Path("/confirmar/reinstalacion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmarReinstalacion(@Context HttpHeaders headers, PromotorPrestamoModel request) {

        try {
            String tokenSeguridad = this.obtenerTokenSeguridad(headers.getRequestHeaders());
            request.setToken(tokenSeguridad);

            ServiceDefinition[] step1 = {capacidadSistrap};

            Message<PromotorPrestamoModel> response1 = capacidadSistrap.executeSteps(step1, new Message<>(request));

            Double capacidadReal = response1.getPayload().getCapacidad().getImpCapacidadTotal();

            if (request.getPrestamosRecuperacionArreglo() != null &&
                !request.getPrestamosRecuperacionArreglo().isEmpty()) {

                for (PrestamoRecuperacion p : request.getPrestamosRecuperacionArreglo()) {
                    capacidadReal += p.getCanDescuentoMensual();
                }
                response1.getPayload().getCapacidad().setImpCapacidadTotal(capacidadReal);
            }
            else
                throw new RegistrarReinstalacionEndPointException(
                    RegistrarReinstalacionEndPointException.ERROR_SOLICITUD_SIN_PRESTAMOS_RECUPERACION);

        } catch (VariableMessageException e) {
            return toResponse(new Message(null, ServiceStatusEnum.PARTIAL_CONTENT,
                new PartialContentFlowException(e.getMessage()), null));
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.confirmarReinstalacion request = [" +
                request + "]", e);

            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                new RegistrarReinstalacionEndPointException(
                    RegistrarReinstalacionEndPointException.ERROR_DESCONOCIDO),null));
        }

        try {
            ServiceDefinition[] step2 = {condicionesPrestamoReinstalacionService};

            condicionesPrestamoReinstalacionService.executeSteps(step2, new Message<>(request));

            return Response.ok().build();
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPrestamoEndPoint.confirmarReinstalacion request = [" +
                    request + "]", e);

            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION,
                    new RegistrarReinstalacionEndPointException(
                            RegistrarReinstalacionEndPointException.ERROR_DESCONOCIDO),null));
        }
    }
}

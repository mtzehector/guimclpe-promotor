/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.ArrayList;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.enums.MaxHorasFechaVigenciaEnum;
import mx.gob.imss.dpes.common.exception.FechaVigenciaException;
import mx.gob.imss.dpes.common.enums.TipoCronTareaEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;
import mx.gob.imss.dpes.interfaces.bitacora.model.TareaAccion;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CronTareaClient;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;
import mx.gob.imss.dpes.promotorfront.restclient.EntidadFinancieraClient;
import org.eclipse.microprofile.config.Config;

/**
 *
 * @author gabriel.rios
 */
@Provider
public class CronJobService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private CronTareaClient cronTareaClient;
    @Inject
    @RestClient
    private SolicitudClient solicitudClient;
    @Inject
    @RestClient
    private EntidadFinancieraClient entidadFinancieraClient;
    @Inject
    private Config config;
    @Inject
    CorreoComponentService correoComponentService;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        log.log(Level.INFO, ">>>CronJobService request.getPayload()= {0}", request.getPayload());
        log.log(Level.INFO, ">>>CronJobService prestamo={0}", request.getPayload().getPrestamo());
        log.log(Level.INFO, ">>>CronJobService prestamo.getTipoCredito={0}", request.getPayload().getPrestamo().getTipoCredito());
        boolean isMejorOferta = calculateMejorOferta(request.getPayload());
        log.log(Level.INFO, ">>>CronJobService isMejorOferta={0}", isMejorOferta);
        int tipoCredito = request.getPayload().getPrestamo().getTipoCredito().getId().intValue();
        Long cveSolicitud = request.getPayload().getSolicitud().getId();
        CronTarea cronTareaResponse = null;
        SolicitudModel solicitud = request.getPayload().getSolicitud();
        int maxHours = 0;
        /*    
        public enum TipoCreditoEnum {
        NUEVO(1L,"Nuevo"),
        RENOVACION(2L,"Renovación"),
        COMPRA_CARTERA(3L,"Compra de cartera"),
        MIXTO(6L,"Mixto");
        
    PROMOTOR_NUEVO(2L, "Simulacion Promotor"),
    PROMOTOR_RENOVACION(4L, "Simulacion Renovacion Promotor"),
    PROMOTOR_RENOVACIONES_MEJOR_OPCION(8L, "Promotor Misma EF(Renovaciones) Mejor opcion"),
    PROMOTOR_COMPRA_CARTERA_MIXTO_PEOR_OPCION(9L, "Promotor Compra de Cartera o Mixto Peor opcion"),
    PROMOTOR_RENOVACIONES_PEOR_OPCION(10L, "Promotor Misma EF(Renovaciones) Peor Opcion"),
    ;*/
        if (request.getPayload().getPrestamosRecuperacionArreglo() != null && !request.getPayload().getPrestamosRecuperacionArreglo().isEmpty()) {
            if (!isMejorOferta) {
                switch (tipoCredito) {
                    //TipoCreditoEnum.COMPRA_CARTERA
                    //TipoCreditoEnum.MIXTO
                    case 3:
                    case 6:
                        cronTareaResponse = fireCron(TipoCronTareaEnum.PROMOTOR_COMPRA_CARTERA_MIXTO_PEOR_OPCION.getTipo(), cveSolicitud);
                        this.sendEmailCompraCarteraAdminEF(request.getPayload());
                        maxHours = MaxHorasFechaVigenciaEnum.MAX_PEOR_OPCION.toValue().intValue();
                        break;
                    //TipoCreditoEnum.RENOVACION
                    case 2:
                        cronTareaResponse = fireCron(TipoCronTareaEnum.PROMOTOR_RENOVACION.getTipo(), cveSolicitud);
                        maxHours = MaxHorasFechaVigenciaEnum.MAX_MEJOR_OPCION.toValue().intValue();
                        break;
                    default:
                        break;
                }
            } else {
                switch (tipoCredito) {
                    //TipoCreditoEnum.COMPRA_CARTERA
                    //TipoCreditoEnum.MIXTO
                    case 3:
                    case 6:
                        cronTareaResponse = fireCron(TipoCronTareaEnum.PROMOTOR_COMPRA_CARTERA_MIXTO_MEJOR_OPCION.getTipo(), cveSolicitud);
                        this.sendEmailCompraCarteraAdminEF(request.getPayload());
                        maxHours = MaxHorasFechaVigenciaEnum.MAX_MEJOR_OPCION.toValue().intValue();
                        break;
                    //TipoCreditoEnum.RENOVACION
                    case 2:
                        cronTareaResponse = fireCron(TipoCronTareaEnum.PROMOTOR_RENOVACION.getTipo(), cveSolicitud);
                        maxHours = MaxHorasFechaVigenciaEnum.MAX_MEJOR_OPCION.toValue().intValue();
                        break;
                    default:
                        break;
                }
            }

        } else {
            cronTareaResponse = fireCron(TipoCronTareaEnum.PROMOTOR_NUEVO.getTipo(), cveSolicitud);
            maxHours = MaxHorasFechaVigenciaEnum.MAX_MEJOR_OPCION.toValue().intValue();
        }
        solicitud.setMaxHoursFechaVigencia(maxHours);
        solicitud = this.updateFechaVigencia(solicitud);
        request.getPayload().setSolicitud(solicitud);
        request.getPayload().setCronTareaResponse(cronTareaResponse);
        return new Message<>(request.getPayload());
    }

    public CronTarea fireCron(Long cveTareaAccion, Long cveSolicitud) {
        CronTarea cronTarea = new CronTarea();
        cronTarea.setCveSolicitud(cveSolicitud);
        TareaAccion tareaAccion = new TareaAccion();
        tareaAccion.setId(cveTareaAccion);
        cronTarea.setTareaAccion(tareaAccion);
        Response load = cronTareaClient.add(cronTarea);
        CronTarea cronTareaResponse = null;
        if (load.getStatus() == 200) {
            cronTareaResponse = load.readEntity(CronTarea.class);
            log.log(Level.INFO, "   >>><<<CronJobService.fireCron cveTareaAccion=" + cveTareaAccion + "  cveSolicitud=" + cveSolicitud + " cronTareaResponse= {0}", cronTareaResponse);

        }
        return cronTareaResponse;
    }

    public boolean calculateMejorOferta(PromotorPrestamoModel request) {
        boolean mejorOferta = false;
        log.log(Level.INFO, ">>>promotorfront|calculateMejorOferta {0}", request);

        // Se ejecuta si contiene prestamos en recuperación.
        if (request.getPrestamosRecuperacionArreglo() != null) {
            if (!request.getPrestamosRecuperacionArreglo().isEmpty()) {
                for (PrestamoRecuperacion p : request.getPrestamosRecuperacionArreglo()) {
                    if (p.getMejorOferta() != null && p.getMejorOferta() > 0) {
                        mejorOferta = true;
                        break;
                    }
                }
            }
        }
        return mejorOferta;
    }

    protected void sendEmailCompraCarteraAdminEF(PromotorPrestamoModel request) {
        if (!request.getPrestamosRecuperacionArreglo().isEmpty()) {
            int i = 0;
            for (PrestamoRecuperacion prestamoRec : request.getPrestamosRecuperacionArreglo()) {
                String numEntidadFinanciera = prestamoRec.getNumEntidadFinanciera();
                log.log(Level.INFO, "   >>><<<CronJobService.sendEmailCompraCarteraAdminEF [" + (i++) + "]prestamoRec==" + prestamoRec);
                log.log(Level.INFO, "   >>><<<CronJobService.sendEmailCompraCarteraAdminEF [" + (i++) + "]numEntidadFinanciera=" + numEntidadFinanciera);
                if (numEntidadFinanciera != null && numEntidadFinanciera.length() > 0) {
                    String asunto = "Compra de cartera";
                    String nombreComercial = prestamoRec.getNombreComercial();
                    String folioSIPRE = prestamoRec.getNumSolicitudSipre();
                    String correoAdminEF = "";
                    Response response = entidadFinancieraClient.load(numEntidadFinanciera);
                    EntidadFinanciera entidadFinanciera = response.readEntity(EntidadFinanciera.class);
                    if (entidadFinanciera.getCveEntidadFinancieraSipre().equals("0")) {
                        log.log(Level.INFO, "   >>>CORREO ADMIN EF PEOR OFERTA {0}", prestamoRec.getCorreoAdminEF());
                        correoAdminEF = prestamoRec.getCorreoAdminEF();
                    } else {
                        correoAdminEF = entidadFinanciera.getCorreoAdminEF();
                    }
                    ArrayList<String> correos = new ArrayList();
                    correos.add(correoAdminEF);
                    String plantilla = String.format(config.getValue("plantillaParaAdminEF-Aviso_Compra_cartera", String.class),
                            nombreComercial, folioSIPRE);
                    correoComponentService.sendEmail(plantilla, asunto, correos);
                } else {
                    log.log(Level.SEVERE, "ERROR. CronJobService.sendEmailCompraCarteraAdminEF (Prestamo SIN Entidad Financiera, NO sincronizado) prestamoRec=" + prestamoRec);

                }

            }

        }
    }

    protected SolicitudModel updateFechaVigencia(SolicitudModel solicitud) throws BusinessException {
        log.log(Level.INFO, ">>>promotorFront CronJobService.updateFechaVigencia solicitud= {0}", solicitud);
        Response load = solicitudClient.updateFechaVigencia(solicitud);
        if (load.getStatus() == 200) {
            SolicitudModel sol = load.readEntity(SolicitudModel.class);
            sol.setCveEstadoSolicitud(solicitud.getCveEstadoSolicitud());
            return sol;
        } else {
            throw new FechaVigenciaException();
        }
    }
}

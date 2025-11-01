/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.config;

/**
 *
 * @author osiris.hernandez
 */
import mx.gob.imss.dpes.promotorfront.service.ValidaTablaAmortizacionService;

import java.util.Set;
import javax.ws.rs.core.Application;


@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
       
       resources.add(mx.gob.imss.dpes.common.exception.AlternateFlowMapper.class);
        resources.add(mx.gob.imss.dpes.common.exception.BusinessMapper.class);
        resources.add(mx.gob.imss.dpes.common.rule.MontoTotalRule.class);
        resources.add(mx.gob.imss.dpes.common.rule.PagoMensualRule.class);
        resources.add(mx.gob.imss.dpes.promotorfront.endpoint.PromotorEndPoint.class);
        resources.add(mx.gob.imss.dpes.promotorfront.endpoint.PromotorPrestamoEndPoint.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CalcularEdad.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.ComparaRenapoVsSipre.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CompararRenapoVsBdtu.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CompararRenapoVsSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CondicionesDelPrestamo.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CondicionesEF.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CondicionesPrestamoReinstalacionRule.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.CondicionesReinstalacionRule.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.ControlFolio.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.FechaNominal.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.SolicitudVigente.class);
        resources.add(mx.gob.imss.dpes.promotorfront.rule.ValidarEstatusCurp.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.AmortizacionAutoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.AmortizacionDatosBaseService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.BDTUService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CancelaSolicitudService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CapacidadCreditoSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CondicionesPrestamoReinstalacionService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CondicionesPrestamoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.ConsultarEntidadFinancieraLogoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.ControlService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CorreoComponentService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CorreoMontoLiquidarService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CorreoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.CronJobService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.EntidadFinancieraService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.EnvioTokenService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.FolioService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.OperadorService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PensionSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PensionadoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PensionadoSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PersonaService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PrestamoRecuperacionReinstalacionService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PrestamoRecuperacionService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PrestamoReinstalarService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PrestamoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PromotorPorCvePersonalEFService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.PromotorService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.RelacionLaboralService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.RenapoService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.SolicitudHabilitarService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.SolicitudReinstalacionService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.SolicitudService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.SolicitudesVigentesService.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.ConsultaEstatusPrestamoReinstalacionSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.ConsultaPrestamoDescuentoNoAplicadoSistrap.class);
        resources.add(mx.gob.imss.dpes.promotorfront.service.ValidaTablaAmortizacionService.class);
        resources.add(mx.gob.imss.dpes.renapo.endpoint.RenapoCurpEndPoint.class);
        resources.add(mx.gob.imss.dpes.renapo.service.RenapoCurpService.class);
    }
    
}

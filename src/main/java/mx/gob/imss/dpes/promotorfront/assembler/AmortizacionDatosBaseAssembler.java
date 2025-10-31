/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.assembler;

import java.util.logging.Level;
import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.common.enums.TipoSimulacionEnum;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
public class AmortizacionDatosBaseAssembler extends BaseAssembler<PromotorPrestamoModel, AmortizacionInsumos>{
   
    @Override
    public AmortizacionInsumos assemble(PromotorPrestamoModel source){

        try {
            AmortizacionInsumos insumos = new AmortizacionInsumos();
            insumos.setCat(Double.parseDouble(source.getPersonaRequest().getPrestamo().getCat()));
            insumos.setCveSolicitud(source.getSolicitud().getId());
            insumos.setImporteMontoSolicitado(
                Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoSolicitado()));
            insumos.setImporteDescuentoMensual(
                Double.parseDouble(source.getPersonaRequest().getPrestamo().getDescuentoMensual()));
            insumos.setImporteTotalPagar(
                Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoPagar()));
            insumos.setCveTipoSimulacion(TipoSimulacionEnum.DESCUENTO_MENSUAL.toValue());
            insumos.setPlazo(source.getPersonaRequest().getPrestamo().getPlazo().getNumPlazo());
            insumos.setHistorico(0);

            //log.log(Level.INFO, ">>> AmortizacionDatosBaseAssembler return : {0}", insumos);
            return insumos;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR AmortizacionDatosBaseAssembler.assemble: [" + source + "]", e);
        }

        return null;
    }
}

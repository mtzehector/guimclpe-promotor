/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.ControlFolioModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class ControlFolio extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {
    
     @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel input) throws BusinessException {
        try {
            ControlFolioModel controlFolioModel = new ControlFolioModel();
            DateFormat formatoAnio = new SimpleDateFormat("yyyy");
            String anio = formatoAnio.format(input.getSolicitud().getAltaRegistro());
            controlFolioModel.setNumAnio(anio);
            controlFolioModel.setDelegacion(input.getSolicitud().getDelegacion());
            input.setControlFolio(controlFolioModel);
            //log.log(Level.SEVERE, ">>>>>>RULE ControlFolio {0} ", controlFolioModel);
            return input;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR ControlFolio.apply - [" + input + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException (
            RegistrarReinstalacionEndPointException.ERROR_REGLA_CONTROL_FOLIO);
    }
      
}

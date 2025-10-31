/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.model.SolicitudesVigentesModel;

/**
 * @author edgar.arenas
 */
@Provider
public class SolicitudVigente extends BaseRule<SolicitudesVigentesModel, SolicitudesVigentesModel> {
    @Override
    public SolicitudesVigentesModel apply(SolicitudesVigentesModel input) {
        Long[] estados = {
            TipoEstadoSolicitudEnum.INICIADO.toValue(),
            TipoEstadoSolicitudEnum.POR_AUTORIZAR.toValue(),
            TipoEstadoSolicitudEnum.PENDIENTE_MONTO_LIQUIDAR.toValue(),
            TipoEstadoSolicitudEnum.POR_ASIGNAR_PROMOTOR.toValue(),
            TipoEstadoSolicitudEnum.PENDIENTE_CARGA_COMPROBANTE.toValue(),
            TipoEstadoSolicitudEnum.AUTORIZADO.toValue()
        };

        input.setEstados(estados);

        return input;
    }
}

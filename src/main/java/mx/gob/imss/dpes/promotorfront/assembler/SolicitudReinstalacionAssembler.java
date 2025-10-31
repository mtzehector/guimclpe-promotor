package mx.gob.imss.dpes.promotorfront.assembler;

import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.common.enums.OrigenSolicitudEnum;
import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.OrigenSolicitud;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;

import java.util.logging.Level;

public class SolicitudReinstalacionAssembler extends BaseAssembler<PromotorPrestamoModel, SolicitudModel> {
    
    @Override
    public SolicitudModel assemble(PromotorPrestamoModel source) {
        try {
            OrigenSolicitud origen = new OrigenSolicitud();
            origen.setId(OrigenSolicitudEnum.REINSTALACION.getId());

            EstadoSolicitud estado = new EstadoSolicitud();
            estado.setId(TipoEstadoSolicitudEnum.PRE_SIMULACION.getTipo());

            SolicitudModel solicitud = new SolicitudModel();
            solicitud.setCveOrigenSolicitud(origen);
            solicitud.setCveEstadoSolicitud(estado);

            solicitud.setCurp(source.getPensionado().getCveCurp());
            solicitud.setDelegacion(source.getPensionado().getCveDelegacion());
            solicitud.setGrupoFamiliar(source.getPensionado().getIdGrupoFamiliar());
            solicitud.setNss(source.getPensionado().getIdNss());
            solicitud.setRefTrabajador("pensionado");

            solicitud.setCveEntidadFinanciera(source.getPersonaEF().getEntidadFinanciera().getId());

            return solicitud;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR SolicitudReinstalacionAssembler.assemble: [" + source + "]", e);
        }
        return null;
    }
    
}

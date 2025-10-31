/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.assembler;

import java.util.logging.Level;
import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.common.enums.OrigenSolicitudEnum;
import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.OrigenSolicitud;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;

/**
 *
 * @author edgar.arenas
 */
public class SolicitudAssembler extends BaseAssembler<PromotorPrestamoModel, SolicitudModel>{
    
    @Override
    public SolicitudModel assemble(PromotorPrestamoModel source) {
        
        SolicitudModel solicitud = new SolicitudModel();
        OrigenSolicitud origen = new OrigenSolicitud();
        EstadoSolicitud estado = new EstadoSolicitud();
                   
        solicitud.setCurp(source.getPensionado().getCveCurp());
        solicitud.setDelegacion(source.getPensionado().getCveDelegacion());
        solicitud.setGrupoFamiliar(source.getPensionado().getIdGrupoFamiliar());
        solicitud.setNss(source.getPensionado().getIdNss());
        solicitud.setRefTrabajador("pensionado");
        origen.setId(OrigenSolicitudEnum.PRESTAMO_PROMOTOR.getId());
        solicitud.setCveOrigenSolicitud(origen);
        
        estado.setId(TipoEstadoSolicitudEnum.PRE_SIMULACION.getTipo());
        solicitud.setCveEstadoSolicitud(estado);
        
        solicitud.setCveEntidadFinanciera(source.getPersonaEF().getEntidadFinanciera().getId());
        solicitud.setCvePromotor(source.getPersonaEF().getIdPersonaEF());
        if (source.getPrestamosRecuperacionArreglo() != null && !source.getPrestamosRecuperacionArreglo().isEmpty()) {
            for(PrestamoRecuperacion p : source.getPrestamosRecuperacionArreglo()){
                if(p.getMejorOferta() == 1){
                    if(source.getEntidadFinanciera().getId() != null){    
                        solicitud.setCveEntidadFinMejorOferta(source.getEntidadFinanciera().getId().toString());
                    }
                }
            }
            
        }
    
        log.log(Level.INFO,">>>>SolicitudAssembler : {0}", solicitud);
        return solicitud;
        
    }
    
}

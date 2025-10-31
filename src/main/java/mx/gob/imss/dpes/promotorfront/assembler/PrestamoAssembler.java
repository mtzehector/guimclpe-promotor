/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.assembler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.common.enums.TipoCreditoEnum;
import mx.gob.imss.dpes.interfaces.prestamo.model.Prestamo;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
public class PrestamoAssembler extends BaseAssembler<PromotorPrestamoModel, Prestamo>{
    
    @Override
    public Prestamo assemble(PromotorPrestamoModel source) {
        
        Prestamo prestamo = new Prestamo();
        prestamo.setMonto(Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoSolicitado()));
        prestamo.setImpDescNomina(Double.parseDouble(source.getPersonaRequest().getPrestamo().getDescuentoMensual()));
        prestamo.setImpTotalPagar(Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoPagar()));
        try {
        Date fn = new SimpleDateFormat("dd/MM/yyyy").parse(source.getPersonaRequest().getFechaNominal());
        prestamo.setPrimerDescuento(fn);
        } catch (ParseException ex) {
            Logger.getLogger(PersonaAssembler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        prestamo.setRefCuentaClabe(source.getPensionado().getNumClabe());
        switch(source.getPrestamo().getTipoCredito().toValue().intValue()){
            case 1:
                prestamo.setTipoCredito(TipoCreditoEnum.NUEVO);
                break;
            case 2:
                prestamo.setTipoCredito(TipoCreditoEnum.RENOVACION);
                break;
            case 3:
                prestamo.setTipoCredito(TipoCreditoEnum.COMPRA_CARTERA);
                break;
            case 6:
                prestamo.setTipoCredito(TipoCreditoEnum.MIXTO);
                break;
                
        }
        
        prestamo.setSolicitud(source.getSolicitud().getId());
        prestamo.setPromotor(source.getPersonaEF().getIdPersonaEF());
        prestamo.setIdOferta(source.getCondiciones().getId());
        log.log(Level.INFO,">>>PrestamoAssembler: {0}", prestamo);
        return prestamo;
        
    }
    
}

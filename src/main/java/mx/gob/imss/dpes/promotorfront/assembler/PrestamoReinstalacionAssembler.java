package mx.gob.imss.dpes.promotorfront.assembler;

import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.common.enums.TipoCreditoEnum;
import mx.gob.imss.dpes.interfaces.prestamo.model.Prestamo;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
public class PrestamoReinstalacionAssembler extends BaseAssembler<PromotorPrestamoModel, Prestamo>{
    
    @Override
    public Prestamo assemble(PromotorPrestamoModel source) {

        try {
            Prestamo prestamo = new Prestamo();

            Date fn = new SimpleDateFormat("dd/MM/yyyy").parse(source.getPersonaRequest().getFechaNominal());
            prestamo.setPrimerDescuento(fn);

            prestamo.setTipoCredito(TipoCreditoEnum.RENOVACION);

            prestamo.setMonto(Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoSolicitado()));
            prestamo.setImpDescNomina(Double.parseDouble(source.getPersonaRequest().getPrestamo().getDescuentoMensual()));
            prestamo.setImpTotalPagar(Double.parseDouble(source.getPersonaRequest().getPrestamo().getMontoPagar()));

            prestamo.setRefCuentaClabe(source.getPensionado().getNumClabe());

            prestamo.setSolicitud(source.getSolicitud().getId());
            prestamo.setIdOferta(source.getOferta().getId());

            return prestamo;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PrestamoReinstalacionAssembler.assemble: [" + source + "]", e);
        }

        return null;
    }
    
}

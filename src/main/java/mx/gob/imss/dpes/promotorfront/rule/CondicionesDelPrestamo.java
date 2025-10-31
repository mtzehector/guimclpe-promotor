/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.common.rule.CatRule;
import mx.gob.imss.dpes.common.rule.PagoMensualRule;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesDescuentoException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesMontoTotalException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CondicionesDelPrestamo extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

    private Double toComparar(double valor) {
        Long val = Math.round(valor * 100d);
        val = val / 100;
        return val.doubleValue();
    }

    private Long toCompararCat(double valor) {
        return Math.round(valor * 100d);

    }

    private static final double PRECISION = 0.000001;

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel input) throws BusinessException {

        log.log(Level.INFO, ">>>>INICIA REGLAS PRESTAMO<<<<");

        Double cat = Double.parseDouble(input.getPersonaRequest().getPrestamo().getCat());
        log.log(Level.INFO, ">>>>cat: {0}", cat);
//        Double montoTotal = Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoPagar());
//        log.log(Level.INFO,">>>>montoTotal: {0}", montoTotal);
//        Double descuentoMensual = Double.parseDouble(input.getPersonaRequest().getPrestamo().getDescuentoMensual());
//        log.log(Level.INFO,">>>>descuentoMensual: {0}", descuentoMensual);
        Double montoSolicitado = Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado());
        log.log(Level.INFO, ">>>>montoSolicitado: {0}", montoSolicitado);
        Integer plazo = input.getPersonaRequest().getPrestamo().getPlazo().getNumPlazo();
        log.log(Level.INFO, ">>>>plazo: {0}", plazo);
        Double catEntidad = input.getCondiciones().getCat();
        log.log(Level.INFO, ">>>>catEntidad: {0}", catEntidad);
        Double catIMSS = input.getCondiciones().getTasaAnual();
        log.log(Level.INFO, ">>>>catIMSS: {0}", catIMSS);
        log.log(Level.INFO, ">>>>capacidadCredito: {0}", input.getCapacidad().getImpCapacidadTotal());

        //CAT
        if (cat > catEntidad || cat > catIMSS) {
            input.setError("err003");
            return input;
        }

        //Descuento Mensual
        PagoMensualRule.Input inputDescuento = new PagoMensualRule().new Input(montoSolicitado, plazo, cat / 100d);
        PagoMensualRule desc = new PagoMensualRule();
        PagoMensualRule.Output outDescuento = desc.apply(inputDescuento);
        log.log(Level.INFO, ">>>>Descuento Mensual {0}", outDescuento.getPagoMensual());

        if (input.getPrestamosRecuperacionArreglo() != null && !input.getPrestamosRecuperacionArreglo().isEmpty()) {
            if (outDescuento.getPagoMensual() > input.getCapacidad().getImpCapacidadTotal()) {
                input.setError("msg00006");
                return input;
                //throw new CondicionesDescuentoException("msg009"); 
            }
        } else {
            if (input.getCapacidad().getImpCapacidadTotal() < outDescuento.getPagoMensual()) {
                input.setError("msg00006");
                return input;
                //throw new CondicionesDescuentoException();
            }
        }

        //Monto total
        Double mt = outDescuento.getPagoMensual() * plazo;
        log.log(Level.INFO, ">>>>Monto Total {0}", mt);
        input.getPersonaRequest().getPrestamo().setDescuentoMensual(String.valueOf(outDescuento.getPagoMensual()));
        input.getPersonaRequest().getPrestamo().setMontoPagar(String.valueOf(mt));

        //Descuento Mensual
//        //Monto Total
//        Double totalPagar = plazo * descuentoMensual;
//        log.log(Level.INFO,"TotalPagar {0}", totalPagar);
//        
//        if(Math.abs(totalPagar - montoTotal) > PRECISION){
//            throw new CondicionesMontoTotalException();
//        }
        //Capacidad Total
//        if(descuentoMensual > input.getCapacidad().getImpCapacidadTotal()){
//            input.setError("err002");
//            return input;
//        }
        //CAT   
//        CatRule.Input inputCat = new CatRule().new Input(montoSolicitado, plazo, descuentoMensual);
//        CatRule rule = new CatRule();
//        CatRule.Output output = rule.apply(inputCat);
//        log.log(Level.INFO,"Cat Salida RULE {0}", output);
//        
//        // La comparación de los CATs se hara con dos decimales, por lo que primero
//        // se redondea antes de campararlo
//        Double catRule = output.getCat() * 100d;
//        log.log(Level.INFO, "### Cat Redondeado RULE {0}", catRule ); 
//        
//       if (cat > catEntidad) {
//            input.setError("err003");
//            return input;
//        } else if (Math.abs(catRule - cat) > PRECISION) {           
//            input.setError("err004");
//            return input;
//        }
        //Descuento Mensual
//        PagoMensualRule.Input inputDescuento = new PagoMensualRule().new Input(montoSolicitado, plazo, catRule/100d);
//        PagoMensualRule desc = new PagoMensualRule();
//        PagoMensualRule.Output outDescuento = desc.apply(inputDescuento);
//        log.log(Level.INFO,"Descuento Salida RULE {0}", outDescuento);
//        if(Double.compare(descuentoMensual, outDescuento.getPagoMensual()) < 0){
//            throw new CondicionesDescuentoException();
//        }else if(Double.compare(descuentoMensual, outDescuento.getPagoMensual()) > 0){
//            throw new CondicionesDescuentoException();
//        }
        return input;

    }
}

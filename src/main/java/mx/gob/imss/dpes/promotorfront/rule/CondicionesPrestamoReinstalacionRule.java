package mx.gob.imss.dpes.promotorfront.rule;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.common.rule.PagoMensualRule;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class CondicionesPrestamoReinstalacionRule extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

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

        Double catSeleccionado = Double.parseDouble(input.getPersonaRequest().getPrestamo().getCat());
        Double montoSolicitado = Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado());
        Integer plazoEnMeses = input.getPersonaRequest().getPrestamo().getPlazo().getNumPlazo();
        Double catEntidadFinanciera = input.getCondiciones().getCat();
        Double catIMSS = input.getCondiciones().getTasaAnual();

        //CAT
        if (catSeleccionado > catEntidadFinanciera || catSeleccionado > catIMSS) {
            input.setError("err007");
            return input;
        }

        //Descuento Mensual
        PagoMensualRule.Input inputDescuento =
            new PagoMensualRule().new Input(montoSolicitado, plazoEnMeses, catSeleccionado / 100d);

        PagoMensualRule desc = new PagoMensualRule();
        PagoMensualRule.Output outDescuento = desc.apply(inputDescuento);

        if (input.getPrestamosRecuperacionArreglo() != null && !input.getPrestamosRecuperacionArreglo().isEmpty()) {
            if (outDescuento.getPagoMensual() > input.getCapacidad().getImpCapacidadTotal()) {
                input.setError("msg365");
                return input;
            }
        } else {
            if (input.getCapacidad().getImpCapacidadTotal() < outDescuento.getPagoMensual()) {
                input.setError("msg365");
                return input;
            }
        }

        //Monto total
        Double mt = outDescuento.getPagoMensual() * plazoEnMeses;
        input.getPersonaRequest().getPrestamo().setDescuentoMensual(String.valueOf(outDescuento.getPagoMensual()));
        input.getPersonaRequest().getPrestamo().setMontoPagar(String.valueOf(mt));

        return input;

    }
}

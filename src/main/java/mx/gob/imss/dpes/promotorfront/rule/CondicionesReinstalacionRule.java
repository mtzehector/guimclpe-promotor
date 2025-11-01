package mx.gob.imss.dpes.promotorfront.rule;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class CondicionesReinstalacionRule extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel input) throws BusinessException {
        try {
            CalcularEdad regla = new CalcularEdad();
            CalcularEdad.Input entrada = regla.new Input(input.getPensionado().getFecNacimiento());
            CalcularEdad.Output salida = regla.apply(entrada);
            Integer edadPensionado = salida.getEdad();

            if (Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado()) >
                    input.getCondicionesEF().getMontoMaximo()) {

                input.setError("msg359");
                return input;
            }

            if (Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado()) <
                    input.getCondicionesEF().getMontoMinimo()) {

                input.setError("msg358");
                return input;
            }

            if (edadPensionado > input.getCondicionesEF().getEdadLimite()) {
                input.setError("msg357");
                return input;
            }

            return input;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CondicionesReinstalacionRule.apply = [" + input + "]", e);
        }

        return null;
    }
}

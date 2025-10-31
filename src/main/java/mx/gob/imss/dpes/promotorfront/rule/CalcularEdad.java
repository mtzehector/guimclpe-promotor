/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import lombok.Getter;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.common.rule.BaseRule;
import static mx.gob.imss.dpes.promotorfront.rule.CompararRenapoVsBdtu.DATE_FORMAT;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CalcularEdad extends BaseRule<CalcularEdad.Input, CalcularEdad.Output> {
    
    final static DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    Date fecNac = null;
    
    @Override
    public Output apply(Input input) {

        LocalDate fecActual = LocalDate.now();
        LocalDate fecNacimiento = input.fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period periodo = Period.between(fecNacimiento, fecActual);
        return new Output(periodo.getYears());
    }

    public class Input extends BaseModel {

        @Getter
        private final transient Date fechaNacimiento;

        public Input(String fechaNacimiento) {
            try {
                fecNac = format.parse(fechaNacimiento);
            } catch (ParseException ex) {
                Logger.getLogger(CalcularEdad.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.fechaNacimiento = fecNac;
        }
    }

    public class Output extends BaseModel {

        @Getter
        private final transient int edad;

        public Output(int edad) {
            this.edad = edad;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.exeption;

import java.util.List;
import mx.gob.imss.dpes.common.exception.BusinessException;

/**
 *
 * @author edgar.arenas
 */
public class SistrapException extends BusinessException{
    
    public final static String KEY = "err005";
    public final static String PRESTAMO_NO_REINSTALABLE = "msg364";
    public final static String CAPACIDAD_CREDITO_INSUFICIENTE = "msg365";
    public final static String PENSIONADO_SIN_PENSION_VIGENTE = "msg366";
    
    public SistrapException() {
        super(KEY);
    }
    
    public SistrapException(List parameters) {
       super(KEY);
       super.addParameters(parameters);
               
    }
    public SistrapException(String causa) {
        super(causa);
    }
}

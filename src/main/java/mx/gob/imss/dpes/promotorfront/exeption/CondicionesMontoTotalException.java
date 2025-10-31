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
public class CondicionesMontoTotalException extends BusinessException {
    
     public final static String KEY = "msg007";
    
    public CondicionesMontoTotalException() {
        super(KEY);
    }
    
    public CondicionesMontoTotalException(List parameters) {
       super(KEY);
       super.addParameters(parameters);
               
    }
    public CondicionesMontoTotalException(String causa) {
        super(causa);
    }
}

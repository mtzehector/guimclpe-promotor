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
public class CondicionesTasaException extends BusinessException{
    
    //public final static String KEY = "msg005";
    
//    public CondicionesTasaException() {
//        super(KEY);
//    }
//    
//    public CondicionesTasaException(List parameters) {
//       super(KEY);
//       super.addParameters(parameters);
//               
//    }
    public CondicionesTasaException(String causa) {
        super(causa);
    }
}

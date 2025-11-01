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
public class CurpInvalidStatusException extends BusinessException{
    
    public final static String KEY = "msg347";
    
    public CurpInvalidStatusException() {
        super(KEY);
    }
    
    public CurpInvalidStatusException(List parameters) {
       super(KEY);
       super.addParameters(parameters);
               
    }
    public CurpInvalidStatusException(String causa) {
        super(causa);
    }
}

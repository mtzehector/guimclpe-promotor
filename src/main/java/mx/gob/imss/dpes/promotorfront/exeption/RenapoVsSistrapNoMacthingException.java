/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.exeption;

import mx.gob.imss.dpes.common.exception.BusinessException;

/**
 *
 * @author edgar.arenas
 */
public class RenapoVsSistrapNoMacthingException extends BusinessException{
    
    public final static String KEY = "msg059";
    public final static String ERROR_EN_COMPARACION_SISTRAP_VS_RENAPO = "msg363";
    
    public RenapoVsSistrapNoMacthingException() {
        super(KEY);
    }

    public RenapoVsSistrapNoMacthingException(String causa) {
        super(causa);
    }
}

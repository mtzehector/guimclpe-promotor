/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.exeption;

import mx.gob.imss.dpes.common.exception.BusinessException;

/**
 *
 * @author juanf.barragan
 */
public class RelacionLaboralException extends BusinessException{
    private static final String KEY = "err006";
    
    public RelacionLaboralException(){super(KEY);}
}

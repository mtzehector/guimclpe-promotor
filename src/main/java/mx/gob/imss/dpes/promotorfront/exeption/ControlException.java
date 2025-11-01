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
public class ControlException extends BusinessException{
    private final static String KEY = "err003";
  
  public ControlException() {
    super(KEY);
  }
}

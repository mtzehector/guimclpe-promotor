/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.exeption;

/**
 *
 * @author osiris.hernandez
 */
import mx.gob.imss.dpes.common.exception.BusinessException;

public class PromotorNoExistenteExeption extends BusinessException {
  private static final String KEY = "msg003";

  public PromotorNoExistenteExeption() {super(KEY);}
}

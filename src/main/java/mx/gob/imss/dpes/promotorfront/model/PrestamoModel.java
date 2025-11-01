/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.model;

import lombok.Data;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.Plazo;

/**
 *
 * @author edgar.arenas
 */
@Data
public class PrestamoModel extends BaseModel {
    
  private String montoSolicitado;
  private Plazo plazo;
  private String cat;
  private String descuentoMensual;
  private String montoPagar;
}

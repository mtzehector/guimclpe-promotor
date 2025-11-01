/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.model;

import lombok.Data;
import mx.gob.imss.dpes.common.model.BaseModel;

/**
 *
 * @author edgar.arenas
 */
@Data
public class BDTUOut extends BaseModel{
    
  private Long id;
  private Long cveIdPersona;
  private String curp;
  
  private String nombre;
  private String primerApellido;
  private String segundoApellido;
  private String correoElectronico;
  private String telefono;
  private String nss;
  private String fechaNacimiento;
  private Sexo sexo;
}

class Sexo extends BaseModel {
    private String descripcion;
    private String genero;
    private Long idSexo;
    
}


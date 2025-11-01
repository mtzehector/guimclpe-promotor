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
public class RenapoResponse extends BaseModel {
    
 private Integer anioReg;
 private String apellido1;
 private String apellido2;
 private Integer codigoError;
 private String crip;
 private String curp;
 private String cveEntidadEmisora;
 private Integer cveMunicipio;
 private String desEntidadNac;
 private String desEntidadRegistro;
 private String desEstatusCURP;
 private String desMunicipio;
 private Integer docProbatorio;
 private String estatusCURP;
 private String fechNac;
 private Integer foja;
 private Integer folioCarta;
 private Integer libro;
 private String message;
 private String nacionalidad;
 private String nombres;
 private Integer numActa;
 private Integer numEntidadReg;
 private Integer numRegExtranjeros;
 private String sexo;
 private String statusOper;
 private Integer tipoError;
 private Integer tomo;
    
}

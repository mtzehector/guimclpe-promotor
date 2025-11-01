/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.model;

import lombok.Getter;
import lombok.Setter;
import mx.gob.imss.dpes.common.model.BaseModel;

/**
 *
 * @author edgar.arenas
 */
public class ControlFolioModel extends BaseModel {
    
    @Getter
    @Setter
    private String numAnio;
    @Getter
    @Setter
    private String delegacion;
    @Getter
    @Setter
    private String altaRegistro;
    @Getter
    @Setter
    private String bajaRegistro;
    @Getter
    @Setter
    private String actualizadoRegistro;
    @Getter
    @Setter
    private String id;  
    @Getter
    @Setter
    private String numMes;
    
    @Getter
    @Setter
    private String numConsecutivo;
    
}

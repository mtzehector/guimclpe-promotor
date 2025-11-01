/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import lombok.Data;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.support.config.CustomDateDeserializer;
import mx.gob.imss.dpes.support.config.CustomDateSerializer;

/**
 *
 * @author edgar.arenas
 */
@Data
public class PersonaRequest extends BaseModel{
    
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String telLocal;
    private String telCelular;
    private String correoElectronico;
    private String curp;
    private String nss;
    private Short cveSexo;
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date fecNacimiento;
    private Integer indRegistro;
    private String flat;
    private String fechaNominal;
    private PrestamoModel prestamo;
}

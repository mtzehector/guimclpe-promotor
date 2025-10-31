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
import lombok.Getter;
import lombok.Setter;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.OrigenSolicitud;
import mx.gob.imss.dpes.support.config.CustomDateDeserializer;
import mx.gob.imss.dpes.support.config.CustomDateSerializer;

/**
 *
 * @author edgar.arenas
 */
@Data
public class SolicitudModel extends BaseModel {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String numFolioSolicitud;

    @Getter
    @Setter
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date fecVigenciaFolio;
    @Getter
    @Setter
    private String curp;

    @Getter
    @Setter
    private String nss;
    @Getter
    @Setter

    private String grupoFamiliar;
    @Getter
    @Setter
    private String delegacion;
    @Getter
    @Setter
    private String subDelegacion;
    @Getter
    @Setter
    private String refTrabajador;
    @Getter
    @Setter
    private Long cveEntidadFederativa;
    @Getter
    @Setter
    private String motivoBajaSolicitud;
    @Getter
    @Setter
    private EstadoSolicitud cveEstadoSolicitud;
    @Getter
    @Setter
    private OrigenSolicitud cveOrigenSolicitud;
    @Getter
    @Setter
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date altaRegistro;
    @Getter
    @Setter
    private Long cveEntidadFinanciera;
    @Getter
    @Setter
    private String cveEntidadFinMejorOferta;
    @Getter
    @Setter
    private Long cvePromotor;
    @Getter
    @Setter
    int maxHoursFechaVigencia;
}

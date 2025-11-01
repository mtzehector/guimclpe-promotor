package mx.gob.imss.dpes.promotorfront.model;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.common.personaef.model.PersonaEF;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.CondicionesEntidadFinanciera;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.Oferta;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.interfaces.prestamo.model.Prestamo;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.interfaces.renapo.model.RenapoCurpRequest;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.bdtu.BDTURequest;
import mx.gob.imss.dpes.interfaces.sipre.model.Capacidad;
import mx.gob.imss.dpes.interfaces.sipre.model.PrestamoRecuperado;
import mx.gob.imss.dpes.interfaces.sistrap.model.ConsultaPensionesResponse;
import mx.gob.imss.dpes.interfaces.sistrap.model.Pensionado;

/**
 *
 * @author edgar.arenas
 */
@Data
@NoArgsConstructor
public class PromotorPrestamoModel extends BaseModel {
    @Getter
    @Setter
    private String curp;
    @Getter
    @Setter
    private String nss;
    @Getter
    @Setter
    private String error;
    @Getter
    @Setter
    private RenapoResponse renapo;
    @Getter
    @Setter
    private RenapoCurpRequest renapoRequest;
    @Getter
    @Setter
    private BDTUModel bdtu;
    @Getter
    @Setter
    private BDTURequest bdtuRequest;
    @Getter
    @Setter
    private Pensionado pensionado;
    @Getter
    @Setter
    private ConsultaPensionesResponse pensiones;
    @Getter
    @Setter
    private Capacidad capacidad;
    @Getter
    @Setter
    private PersonaModel persona;
    @Getter
    @Setter
    private PersonaRequest personaRequest;
    @Getter
    @Setter
    private SolicitudModel solicitud;
    @Getter
    @Setter
    private ControlFolioModel controlFolio;
    @Getter
    @Setter
    private PersonaEF personaEF;
    @Getter
    @Setter
    private Prestamo prestamo;
    @Getter
    @Setter
    private CondicionesOfertaModel condiciones;
    @Getter
    @Setter
    private AmortizacionInsumos amortizacionInsumos;
    @Getter
    @Setter
    private List<PrestamoRecuperacion> prestamosRecuperacionArreglo;
    @Getter
    @Setter
    private CronTarea cronTareaResponse;
    @Getter
    @Setter
    private String cveEntidadFinanciera;
    @Getter
    @Setter
    private CondicionesEntidadFinanciera condicionesEF;
    @Getter
    @Setter
    private String cveEntidadFinSIPRE;
    @Getter
    @Setter
    private EntidadFinanciera entidadFinanciera;
    @Getter
    @Setter
    private Oferta oferta;
    @Getter
    @Setter
    private RequestPromotorModel promotor;
    @Getter
    @Setter
    private Long sesion;
    @Getter
    @Setter
    private Boolean flagError;
    @Getter
    @Setter
    private String idSolPrestamo;
    @Getter
    @Setter
    private PrestamoVigenteModel prestamoVigente;
    @Getter
    @Setter
    private Boolean validacionTablaAmortizacionExitosa;
    @Getter
    @Setter
    //token de NetIQ
    private String token;
}

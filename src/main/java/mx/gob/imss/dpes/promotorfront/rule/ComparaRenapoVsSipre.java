package mx.gob.imss.dpes.promotorfront.rule;

import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.common.enums.BitacoraEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.interfaces.bitacora.model.Bitacora;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.BitacoraBackClient;
import mx.gob.imss.dpes.promotorfront.restclient.PensionadoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * @author luisr.rodriguez
 */
@Provider
public class ComparaRenapoVsSipre extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {
    @Inject
    @RestClient
    private BitacoraBackClient bitacoraClient;
    @Inject
    @RestClient
    private PensionadoClient pensionadoClient;

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel request) throws BusinessException {
        try {
            Boolean requiresUpdate = false;

            //Comparacion NOMBRE
            if (request.getRenapoRequest().getRenapoCurpOut().getNombres().length() > 40) {
                if (!request.getRenapoRequest().getRenapoCurpOut().getNombres()
                        .substring(0, 40).equals(request.getPersona().getNombre())) {
                    log.log(Level.WARNING, ">>> Incosistencia CompararRenapoVsSipre Nombre(s)");
                    request.getPersona().setNombre(request.getRenapoRequest().getRenapoCurpOut().getNombres());
                    requiresUpdate = true;
                }
            } else {
                if (!request.getRenapoRequest().getRenapoCurpOut().getNombres()
                        .equals(request.getPersona().getNombre())) {
                    log.log(Level.WARNING, ">>> Incosistencia CompararRenapoVsSipre Nombre(s)");
                    request.getPersona().setNombre(request.getRenapoRequest().getRenapoCurpOut().getNombres());
                    requiresUpdate = true;
                }
            }

            //Comparacion APELLIDO PATERNO
            if (!request.getRenapoRequest().getRenapoCurpOut().getApellido1()
                    .equals(request.getPersona().getPrimerApellido())) {
                log.log(Level.WARNING, ">>> Exception CompararRenapoVsSistrap ApellidoPaterno");
                request.getPersona().setPrimerApellido(request.getRenapoRequest().getRenapoCurpOut().getApellido1());
                requiresUpdate = true;
            }

            //Comparacion APELLIDO MATERNO
            if (request.getRenapoRequest().getRenapoCurpOut().getApellido2() != null
                    && !request.getRenapoRequest().getRenapoCurpOut().getApellido2().equals("")) {

                if (!request.getRenapoRequest().getRenapoCurpOut().getApellido2()
                        .equals(request.getPersona().getSegundoApellido())) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap ApellidoMaterno");
                    request.getPersona().setSegundoApellido(request.getRenapoRequest().getRenapoCurpOut().getApellido2());
                    requiresUpdate = true;
                }

            }

            if (requiresUpdate) {
                //Actualización de datos pensionado
                Response responseUpdateDatos = pensionadoClient.updateDatos(request.getPersona());

                if (responseUpdateDatos.getStatus() == Response.Status.OK.getStatusCode()) {

                    //Registro de actividad en bitácora
                    Bitacora bitacora = new Bitacora();
                    bitacora.setCurp(request.getCurp());
                    bitacora.setSesion(request.getSesion());
                    bitacora.setTipo(BitacoraEnum.ACTUALIZACION_NOMBRE_PENSIONADO);
                    bitacoraClient.create(bitacora);
                }
            }

            return request;
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR ComparaRenapoVsSipre.apply = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.exeption.RenapoVsSistrapNoMacthingException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 * @author edgar.arenas
 */
@Provider
public class CompararRenapoVsSistrap extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

    Date renapoFecNac = null;
    Date sistrapFecNac = null;

    final static String DATE_FORMAT = "dd/MM/yyyy";
    final static DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel request) throws BusinessException {
        try {
            //log.log(Level.INFO, ">>>>>>RULE CompararRenapoVsSistrap {0} ", request);
            request.setFlagError(Boolean.FALSE);

            //Comparacion CURP
            if (!request.getRenapoRequest().getRenapoCurpOut().getCurp().equals(request.getPensionado().getCveCurp())) {
                log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - CURP [{0}] - [{1}]",
                    new Object[]{request.getRenapoRequest().getRenapoCurpOut().getCurp(),
                        request.getPensionado().getCveCurp()});
                request.setFlagError(Boolean.TRUE);
                return request;
            }

            //Comparacion NSS
            if (!request.getNss().equals(request.getPensionado().getIdNss())) {
                log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - NSS [{0}] - [{1}]",
                    new Object[]{request.getNss(), request.getPensionado().getIdNss()});
                request.setFlagError(Boolean.TRUE);
                return request;
            }
            //Comparacion NOMBRE
            if (request.getPensionado() != null && request.getPensionado().getNomNombre() != null)
                request.getPensionado().setNomNombre(fixSistrapString(request.getPensionado().getNomNombre()));

            if (request.getRenapoRequest().getRenapoCurpOut().getNombres().length() > 40) {
                if (!request.getRenapoRequest().getRenapoCurpOut().getNombres().substring(0, 40).equals(
                        request.getPensionado().getNomNombre())) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - Nombres [{0}] - [{1}]",
                        new Object[]{request.getRenapoRequest().getRenapoCurpOut().getNombres().substring(0, 40),
                            request.getPensionado().getNomNombre()});
                    request.setFlagError(Boolean.TRUE);
                    return request;
                } else {
                    request.getPensionado().setNomNombre(request.getRenapoRequest().getRenapoCurpOut().getNombres());
                }
            } else {
                if (!request.getRenapoRequest().getRenapoCurpOut().getNombres().equals(
                        request.getPensionado().getNomNombre())) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - Nombres [{0}] - [{1}]",
                        new Object[]{request.getRenapoRequest().getRenapoCurpOut().getNombres(),
                            request.getPensionado().getNomNombre()});
                    request.setFlagError(Boolean.TRUE);
                    return request;
                } else {
                    request.getPensionado().setNomNombre(request.getRenapoRequest().getRenapoCurpOut().getNombres());
                }
            }

            //Comparacion APELLIDO PATERNO
            if (request.getPensionado() != null && request.getPensionado().getNomApellidoPaterno() != null)
                request.getPensionado().setNomApellidoPaterno(
                    fixSistrapString(request.getPensionado().getNomApellidoPaterno()));

            if (!request.getRenapoRequest().getRenapoCurpOut().getApellido1().equals(
                    request.getPensionado().getNomApellidoPaterno())) {
                log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - ApellidoPaterno [{0}] - [{1}]",
                    new Object[]{request.getRenapoRequest().getRenapoCurpOut().getApellido1(),
                        request.getPensionado().getNomApellidoPaterno()});
                request.setFlagError(Boolean.TRUE);
                return request;
            }
            //Comparacion APELLIDO MATERNO
            if (request.getPensionado() != null && request.getPensionado().getNomApellidoMaterno() != null)
                request.getPensionado().setNomApellidoMaterno(
                    fixSistrapString(request.getPensionado().getNomApellidoMaterno()));

            if (request.getRenapoRequest().getRenapoCurpOut().getApellido2() != null && !request.getRenapoRequest().
                    getRenapoCurpOut().getApellido2().equals("")) {

                if (!request.getRenapoRequest().getRenapoCurpOut().getApellido2().equals(
                        request.getPensionado().getNomApellidoMaterno())) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - ApellidoMaterno [{0}] - [{1}]",
                        new Object[]{request.getRenapoRequest().getRenapoCurpOut().getApellido2(),
                            request.getPensionado().getNomApellidoMaterno()});
                    request.setFlagError(Boolean.TRUE);
                    return request;
                }
            } else {
                if (request.getPensionado().getNomApellidoMaterno() != null) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply SIN ApellidoMaterno");
                    request.setFlagError(Boolean.TRUE);
                    return request;
                }
            }

            //Comparacion SEXO
            if ((request.getRenapoRequest().getRenapoCurpOut().getSexo() != null &&
                    !request.getRenapoRequest().getRenapoCurpOut().getSexo().isEmpty()) &&
                    (request.getPensionado().getSexo() != null && !request.getPensionado().getSexo().isEmpty())) {
                String sexoRenapo = request.getRenapoRequest().getRenapoCurpOut().getSexo().equals("HOMBRE") ? "1" : "2";

                if (!sexoRenapo.equals(request.getPensionado().getSexo())) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - Sexo [{0}] - [{1}]",
                        new Object[]{sexoRenapo, request.getPensionado().getSexo()});
                    request.setFlagError(Boolean.TRUE);
                    return request;
                }
            } else {
                request.setFlagError(Boolean.TRUE);
                return request;
            }

            //Comparacion FECHA NACIMIENTO
            if (request.getPensionado().getFecNacimiento() != null &&
                    !request.getPensionado().getFecNacimiento().equals("")) {

                try {
                    renapoFecNac = format.parse(request.getRenapoRequest().getRenapoCurpOut().getFechNac());
                    sistrapFecNac = format.parse(request.getPensionado().getFecNacimiento());
                } catch (Exception e) {
                    log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - Error en format fechas");
                    request.setFlagError(Boolean.TRUE);
                    return request;
                }

            } else {
                if (request.getRenapoRequest().getRenapoCurpOut().getFechNac() != null &&
                        !request.getRenapoRequest().getRenapoCurpOut().getFechNac().equals("")) {

                    request.getPensionado().setFecNacimiento(request.getRenapoRequest().getRenapoCurpOut().getFechNac());
                    try {
                        renapoFecNac = format.parse(request.getRenapoRequest().getRenapoCurpOut().getFechNac());
                        sistrapFecNac = format.parse(request.getRenapoRequest().getRenapoCurpOut().getFechNac());
                    } catch (Exception e) {
                        log.log(Level.SEVERE,
                            ">>> Exception CompararRenapoVsSistrap.apply - Error en format fechas");
                        request.setFlagError(Boolean.TRUE);
                        return request;
                    }
                } else {
                    log.log(Level.SEVERE,
                        ">>> CompararRenapoVsSistrap.apply fecha de nacimiento renapo es null o vacia");
                    request.setFlagError(Boolean.TRUE);
                    return request;
                }
            }

            if (renapoFecNac.compareTo(sistrapFecNac) != 0) {
                log.log(Level.SEVERE, ">>> Exception CompararRenapoVsSistrap.apply - Fecha [{0}] - [{1}]",
                    new Object[]{renapoFecNac, sistrapFecNac});

                request.setFlagError(Boolean.TRUE);
                return request;
            }

            return request;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CompararRenapoVsSistrap.apply = [" + request + "]", e);
        }

        throw new RenapoVsSistrapNoMacthingException(
            RenapoVsSistrapNoMacthingException.ERROR_EN_COMPARACION_SISTRAP_VS_RENAPO);
    }

    private String fixSistrapString(String sistrapString) {
        if (sistrapString != null && !sistrapString.trim().isEmpty()) {
            sistrapString = sistrapString.trim().replace("?", "Ñ");
            sistrapString = sistrapString.replace("#", "Ñ");
        }
        return sistrapString;
    }
}

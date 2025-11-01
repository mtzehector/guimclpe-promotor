/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.exeption.CurpInvalidStatusException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 * @author edgar.arenas
 */
@Provider
public class ValidarEstatusCurp extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel request) throws BusinessException {

        //log.log(Level.SEVERE, ">>>promotorfront|ValidarEstatusCurp {0} ", request);
        String estatusCURP = request.getRenapoRequest().getRenapoCurpOut().getEstatusCURP();

        if (!("AN".equals(estatusCURP)
                || "AH".equals(estatusCURP)
                || "CRA".equals(estatusCURP)
                || "RCN".equals(estatusCURP)
                || "RCC".equals(estatusCURP)
        )) {
            List parameters = new LinkedList();
            parameters.add(request.getRenapoRequest().getRenapoCurpOut().getDesEstatusCURP());
            throw new CurpInvalidStatusException(parameters);
        }

        return request;
    }

}

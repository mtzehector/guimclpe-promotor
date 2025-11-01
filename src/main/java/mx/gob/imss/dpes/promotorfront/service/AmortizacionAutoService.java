/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.enums.TipoSimulacionEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.service.BaseService;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.promotorfront.model.PrestamoVigenteModel;
import mx.gob.imss.dpes.promotorfront.restclient.AmortizacionClient;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoFrontClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import mx.gob.imss.dpes.interfaces.pensionado.model.Pensionado;
import mx.gob.imss.dpes.support.util.FileCSVReader;
import org.eclipse.microprofile.config.Config;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class AmortizacionAutoService extends BaseService {
    
    @Inject
    @RestClient
    private AmortizacionClient client;
    @Inject
    @RestClient
    private PrestamoFrontClient prestamoFrontClient;
    @Inject
    private Config config;
    
    public List<AmortizacionInsumos> execute(int test) throws BusinessException {
        List<String> nssList = new LinkedList();
        List<AmortizacionInsumos> amortizacionInsumosList = new LinkedList();
        String fileName = config.getValue("amortAutoFileName", String.class);
        List<List<String>> nssRowList = FileCSVReader.getRows(fileName);
        log.log(Level.INFO, ">>>AmortizacionAutoService test="+test+"  fileName='"+fileName+"' nssRowList.size="+nssRowList.size());
        for(List<String> nssRow:nssRowList ){
            String nss = nssRow.get(0);
            if(nss!=null && nss.length()>0){
                nssList.add(nss.trim());
            }
        }
        for(String nss:nssList){
            List<PrestamoVigenteModel> prestamosVigentesList = new LinkedList();
            Pensionado pensionado = new Pensionado();
            pensionado.setNss(nss);
            pensionado.setGrupoFamiliar("01");
            try{
                Response load = prestamoFrontClient.prestamosVigentes(pensionado);
                if (load.getStatus() == 200) {
                  prestamosVigentesList = load.readEntity(new GenericType<List<PrestamoVigenteModel>>() {});

                }
            }
            catch(Exception e){
               log.log(Level.SEVERE, " >>><<<AmortizacionAutoService prestamosVigentes ERROR="+e.getMessage());
               e.printStackTrace();
            }
            log.log(Level.INFO, ">>>#####################AmortizacionAutoService nss="+nss+"  prestamosVigentesList.size = {0}", prestamosVigentesList.size());
            for(PrestamoVigenteModel prestamoVigente:prestamosVigentesList){
            
                log.log(Level.INFO, "   >>><<<AmortizacionAutoService prestamosVigentes="+prestamoVigente);
                AmortizacionInsumos datosBase = new AmortizacionInsumos();
                datosBase.setHistorico(1);
                Integer plazo = Integer.parseInt(prestamoVigente.getDescripcionPlazo());
                datosBase.setPlazo(plazo);
                double cat = prestamoVigente.getCat();
                datosBase.setCat(cat);
                datosBase.setCveTipoSimulacion(TipoSimulacionEnum.DESCUENTO_MENSUAL.toValue());
                double importeDescuentoMensual = Double.parseDouble(prestamoVigente.getDescuentoMensual());
                datosBase.setImporteDescuentoMensual(importeDescuentoMensual);
                String folioSipre = prestamoVigente.getIdSolicitud();
                datosBase.setFolioSipre(folioSipre);
                datosBase.setTest(test);
                try{
                    Response loadAmortizacion = client.guardarDatosBaseAmortizacion(datosBase);
                    if (loadAmortizacion.getStatus() == 200) {
                        AmortizacionInsumos amortizacionInsumos = loadAmortizacion.readEntity(AmortizacionInsumos.class);
                        amortizacionInsumosList.add(amortizacionInsumos);
                    }
                }
                catch(Exception e){
                    log.log(Level.SEVERE, " >>><<<AmortizacionAutoService guardarDatosBaseAmortizacion ERROR="+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return amortizacionInsumosList;
    }
}

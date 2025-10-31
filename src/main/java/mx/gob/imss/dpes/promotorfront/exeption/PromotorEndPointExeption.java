package mx.gob.imss.dpes.promotorfront.exeption;

/**
 *
 * @author osiris.hernandez
 */
import mx.gob.imss.dpes.common.exception.BusinessException;

public class PromotorEndPointExeption extends BusinessException {
  private static final String KEY = "msg000";

  public PromotorEndPointExeption() {super(KEY);}
}

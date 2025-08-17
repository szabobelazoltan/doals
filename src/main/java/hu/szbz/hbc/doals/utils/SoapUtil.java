package hu.szbz.hbc.doals.utils;

import hu.szbz.hbc.doals.endpoints.ws.ResponseBase;
import hu.szbz.hbc.doals.endpoints.ws.ResponseHeaderDto;
import hu.szbz.hbc.doals.exceptions.ServiceException;

import java.util.function.Function;
import java.util.function.Supplier;

public class SoapUtil {

    public static <I, O extends ResponseBase> O processRequest(Function<I, O> businessMethod, Supplier<O> rpFactory, I params) {
        try {
            final O businessRp = businessMethod.apply(params);
            businessRp.setHeader(new ResponseHeaderDto());
            businessRp.getHeader().setSuccess(true);
            return businessRp;
        } catch (ServiceException e) {
            final O errorRp = rpFactory.get();
            errorRp.setHeader(e.toResponseHeader());
            return errorRp;
        }
    }
}

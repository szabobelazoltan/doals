package hu.szbz.hbc.doals.exceptions;

import hu.szbz.hbc.doals.endpoints.ws.ResponseHeaderDto;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 4233643905197275137L;

    private final ErrorCode errorCode;

    public ServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResponseHeaderDto toResponseHeader() {
        final ResponseHeaderDto dto = new ResponseHeaderDto();
        dto.setSuccess(false);
        dto.setResultCode(errorCode.name());
        return dto;
    }
}

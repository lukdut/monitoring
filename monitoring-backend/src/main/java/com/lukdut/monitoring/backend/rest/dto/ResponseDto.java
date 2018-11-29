package com.lukdut.monitoring.backend.rest.dto;

//Immutable
public class ResponseDto {
    private static final ResponseDto OK_RESPONSE = new ResponseDto(true, null);
    private final boolean isSuccess;
    private final String message;

    private ResponseDto(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public static ResponseDto failResponse(String msg){
        return new ResponseDto(false, msg);
    }

    public static  ResponseDto okResponse(){
        return OK_RESPONSE;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}

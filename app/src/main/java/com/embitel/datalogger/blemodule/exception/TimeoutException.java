package com.embitel.datalogger.blemodule.exception;


public class TimeoutException extends BleException {

    public TimeoutException() {
        super(ERROR_CODE_TIMEOUT, "Timeout Exception Occurred!");
    }

}

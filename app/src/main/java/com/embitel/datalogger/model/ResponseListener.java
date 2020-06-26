package com.embitel.datalogger.model;

public interface ResponseListener<T> {
    void success(T t);
    void error(String error);
}

package org.aron.boot.error;

/**
 * @author: Y-Aron
 * @create: 2019-02-11 10:33
 **/
public class MappingException extends Exception {

    private String message;

    public MappingException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

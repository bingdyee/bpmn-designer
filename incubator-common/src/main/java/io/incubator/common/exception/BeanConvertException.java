package io.incubator.common.exception;

/**
 * @author Noa Swartz
 */
public class BeanConvertException extends RuntimeException {

    public BeanConvertException(String message) {
        super(message);
    }

    public BeanConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BeanConvertException() {
        super();
    }

}

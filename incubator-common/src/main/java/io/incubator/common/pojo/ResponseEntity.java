package io.incubator.common.pojo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;

/**
 *
 *
 * @author Noa Swartz
 * @date 2020-04-07
 */
public class ResponseEntity<T> {

    /** 返回码 */
    private int code;
    /** 返回消息提示 */
    private String message;
    /** 返回数据 */
    protected T data;

    public ResponseEntity() {}

    public ResponseEntity(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseEntity(HttpStatus status, T data) {
        this(status.value(), status.getReasonPhrase(), data);
    }

    public ResponseEntity(HttpStatus status) {
        this(status.value(), status.getReasonPhrase(), null);
    }

    public ResponseEntity(int code, String message) {
        this(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static<T> ResponseEntity<T> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static<T> ResponseEntity<T> ok(T data) {
        return new ResponseEntity<>(HttpStatus.OK, data);
    }

    public static<T> ResponseEntity<T> error() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static<T> ResponseEntity<T> error(String message) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static<T> ResponseEntity<T> of(HttpStatus status, String message) {
        return new ResponseEntity<>(status.value(), message);
    }

    public static<T> ResponseEntity<T> of(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public boolean isSucceed() {
        return this.code == HttpStatus.OK.value();
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}

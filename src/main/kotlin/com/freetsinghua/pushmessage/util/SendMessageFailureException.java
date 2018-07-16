package com.freetsinghua.pushmessage.util;

/**
 * @author tsinghua
 * @date 2018/7/13
 */
public class SendMessageFailureException extends RuntimeException {
    public SendMessageFailureException(String message) {
        super(message);
    }
}

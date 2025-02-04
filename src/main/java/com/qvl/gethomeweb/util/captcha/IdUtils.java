package com.qvl.gethomeweb.util.captcha;


public class IdUtils {
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

}

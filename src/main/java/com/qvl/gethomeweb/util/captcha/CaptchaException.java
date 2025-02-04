package com.qvl.gethomeweb.util.captcha;

public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("驗證碼錯誤");
    }
}

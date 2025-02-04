package com.qvl.gethomeweb.util.captcha;

public class CaptchaExpireException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException()
    {
        super("驗證碼已失效");
    }
}

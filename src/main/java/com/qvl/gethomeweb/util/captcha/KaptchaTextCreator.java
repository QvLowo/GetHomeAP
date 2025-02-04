package com.qvl.gethomeweb.util.captcha;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

//驗證碼邏輯 數字乘法
public class KaptchaTextCreator extends DefaultTextCreator
{
    private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String getText()
    {
        Integer result = 0;
        Random random = new Random();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder strBuilder = new StringBuilder();
        result = x * y;
        strBuilder.append(CNUMBERS[x]);
        strBuilder.append("*");
        strBuilder.append(CNUMBERS[y]);
        strBuilder.append("=?@" + result);
        return strBuilder.toString();
    }
}

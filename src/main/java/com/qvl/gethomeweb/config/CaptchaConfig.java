package com.qvl.gethomeweb.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_CONFIG_KEY;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_IMPL;

@Configuration
public class CaptchaConfig
{
    @Bean(name = "captchaProducerMath")
    public DefaultKaptcha getKaptchaBeanMath()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.qvl.gethomeweb.util.captcha.KaptchaTextCreator");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}

    package com.qvl.gethomeweb.controller;
    import com.google.code.kaptcha.Producer;
    import com.qvl.gethomeweb.redis.RedisCache;
    import com.qvl.gethomeweb.util.Base64;
    import com.qvl.gethomeweb.util.Result;
    import com.qvl.gethomeweb.util.captcha.IdUtils;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.util.FastByteArrayOutputStream;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import jakarta.annotation.Resource;

    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    import java.util.concurrent.TimeUnit;

    @Tag(name="驗證碼API")
    @RestController
    @RequestMapping("/public")
    public class CaptchaController {
        @Resource(name = "captchaProducerMath")
        private Producer captchaProducerMath;

        @Autowired
        private RedisCache redisCache;

//        產生驗證碼
        @Operation(summary = "取得驗證碼圖片")
        @GetMapping("/code")
        public Result getCode(HttpServletResponse response) {
            Result result = Result.success();

//            儲存驗證碼資料到redis
            String uuid = IdUtils.simpleUUID();
            String verifyKey = "captcha_codes:" + uuid;
            String capText = captchaProducerMath.createText();
            String capStr = capText.substring(0, capText.lastIndexOf("@"));
            String code = capText.substring(capText.lastIndexOf("@") + 1);
            BufferedImage image = captchaProducerMath.createImage(capStr);
            redisCache.setCacheObject(verifyKey, code, 2, TimeUnit.MINUTES); // 2分鐘內有效

//            序列化Object
            FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
            try {
                ImageIO.write(image, "jpg", outputStream);
            } catch (IOException e) {
                return Result.error(e.getMessage());
            }

            result.put("uuid", uuid);
            result.put("img", Base64.encode(outputStream.toByteArray()));
            return result;
        }
    }

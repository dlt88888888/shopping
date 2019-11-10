package com.leyou.config;

import com.leyou.common.utils.RsaUtils;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {
    private String pubKeyPath;

    private PublicKey publicKey;

    private String cookieName;

    private static final Logger logger= LoggerFactory.getLogger(JwtProperties.class);

    /**
     * 构造方法执行之后执行
     */
    @PostConstruct
    public void init(){
        try {
            this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥失败",e);

            throw new RuntimeException();
        }
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public static Logger getLogger() {
        return logger;
    }
}

package cn.kgc.wxtext.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class WXConfig implements WXPayConfig {
    @Override
    public String getAppID() {
        return "";
    }

    @Override
    public String getMchID() {
        return "";
    }

    @Override
    public String getKey() {
        return "";
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 0;
    }
}

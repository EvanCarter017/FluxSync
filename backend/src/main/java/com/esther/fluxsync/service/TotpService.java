package com.esther.fluxsync.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class TotpService {

    private final GoogleAuthenticator gAuth;

    public TotpService() {
        this.gAuth = new GoogleAuthenticator();
    }

    // 生成一个新的Totp身份验证密钥。
    public GoogleAuthenticatorKey createKey() {
        return gAuth.createCredentials();
    }

    // 生成 TOTP 二维码（PNG 格式字节数组）。
    @SuppressWarnings("CallToPrintStackTrace")
    public byte[] createTotpQrCode(GoogleAuthenticatorKey secretKey, String username) {
        String otp = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                "FluxSync",
                username + "@fluxsync.com",
                secretKey
        ).split("&algorithm=SHA1&digits=6&period=30")[0];

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(otp, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GoogleAuthenticatorKey buildAuthObject(String key) {
        return new GoogleAuthenticatorKey.Builder(key).build();
    }

    // 验证给定的验证码是否与指定的密钥匹配。
    public boolean verifyCode(String secretKey, int code) {
        return gAuth.authorize(secretKey, code);
    }

}

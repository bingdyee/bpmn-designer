package io.incubator.common.security;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Noa Swartz
 * @date 2020-04-09
 */
public class Base64RSA extends RSA {

    @Override
    public void loadPrivateKey(String path) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(RSAReader.readBase64(path));
            privateKey = KeyFactory.getInstance(RSA).generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPublicKey(String path) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(RSAReader.readBase64(path));
            publicKey = KeyFactory.getInstance(RSA).generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportPublicKey(String path) {
        RSAWriter.writeBase64(path, publicKey);
    }

    @Override
    public void exportPrivateKey(String path) {
        RSAWriter.writeBase64(path, privateKey);
    }

}

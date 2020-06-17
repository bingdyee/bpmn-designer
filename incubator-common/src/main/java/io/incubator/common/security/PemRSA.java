package io.incubator.common.security;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author Noa Swartz
 * @date 2020-04-09
 */
public class PemRSA extends RSA {

    @Override
    public void loadPrivateKey(String path) {
        try {
            BigInteger[] pemData = RSAReader.readPem(path);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(pemData[0], pemData[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPublicKey(String path) {
        try {
            BigInteger[] pemData = RSAReader.readPem(path);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(pemData[0], pemData[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportPublicKey(String path) {
        RSAWriter.writePem(path, publicKey);
    }

    @Override
    public void exportPrivateKey(String path) {
        RSAWriter.writePem(path, privateKey);
    }

}

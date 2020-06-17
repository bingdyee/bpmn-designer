package io.incubator.common.security;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

/**
 * @author Noa Swartz
 * @date 2020-04-09
 */
public final class RSAReader {

    public static BigInteger[] readPem(String filePath) throws Exception {
        try(ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {
            BigInteger modulus = (BigInteger) oin.readObject();
            BigInteger exponent = (BigInteger) oin.readObject();
            return new BigInteger[]{modulus, exponent};
        }
    }

    public static byte[] readBase64(String filePath) throws Exception {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----")) {
                    builder.append(line);
                }
            }
            return Base64.getDecoder().decode(builder.toString());
        }
    }

    public static KeyPair loadFromKeyStore(String storeFile, String storePass, String alias, String keyPass, String storeType) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);
        keyStore.load(new FileInputStream(storeFile), storePass.toCharArray());
        Certificate certificate = keyStore.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey)keyStore.getKey(alias, keyPass.toCharArray());
        PublicKey publicKey = certificate.getPublicKey();
        return new KeyPair(publicKey, privateKey);
    }

}

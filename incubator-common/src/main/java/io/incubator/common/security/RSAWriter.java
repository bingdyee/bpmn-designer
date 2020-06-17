package io.incubator.common.security;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @author Noa Swartz
 * @date 2020-04-09
 */
public final class RSAWriter {

    public static final String RSA_PUBLIC_FORMAT =  "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----";
    public static final String RSA_PRIVATE_FORMAT =  "-----BEGIN CERTIFICATE-----\n%s\n-----END CERTIFICATE-----";

    public static void writePem(String filePath, Key key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            BigInteger modulus = null, exponent = null;
            if (key instanceof PublicKey) {
                RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(key, RSAPublicKeySpec.class);
                modulus = publicKeySpec.getModulus();
                exponent = publicKeySpec.getPublicExponent();
            } else if(key instanceof PrivateKey) {
                RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class);
                modulus = privateKeySpec.getModulus();
                exponent = privateKeySpec.getPrivateExponent();
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
                out.writeObject(modulus);
                out.writeObject(exponent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBase64(String filePath, Key key) {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        // format data
        encoded = encoded.replaceAll("(.{64})", "$1\n");
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            String formatStr = key instanceof PublicKey ? RSA_PUBLIC_FORMAT : RSA_PRIVATE_FORMAT;
            fileWriter.write(String.format(formatStr, encoded));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

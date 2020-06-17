package io.incubator.common.security;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * @author Noa Swartz
 * @date 2020-04-09
 */
public abstract class RSA {

    public static final String RSA = "RSA";
    public static final int KEY_SIZE = 2048;
    public static final String SIGNATURE_ALGORITHM = "SHA512withRSA";

    protected PrivateKey privateKey;
    protected PublicKey publicKey;

    /**
     * create random keys
     *
     * @return
     */
    public boolean initRandomKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            // Generate the KeyPair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // Get the public and private key
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            return Boolean.TRUE;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * init KeyPair from store file
     *
     * @param storeFile filePath
     * @param storePass store password
     * @param alias alias
     * @param keyPass key password
     * @param storeType store type
     * @return success ?
     */
    public boolean initFormKeyStore(String storeFile, String storePass, String alias, String keyPass, String storeType) {
        try {
            KeyPair keyPair = RSAReader.loadFromKeyStore(storeFile, storePass, alias, keyPass, storeType);
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    public byte[] encrypt(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] sign(byte[] data) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verify(byte[] data, byte[] sign) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * load private key
     *
     * @param path
     */
    public abstract void loadPrivateKey(String path);

    /**
     * load public key
     *
     * @param path
     */
    public abstract void loadPublicKey(String path);

    /**
     * export public key
     *
     * @param path
     */
    public abstract void exportPublicKey(String path);

    /**
     * export private key
     *
     * @param path
     */
    public abstract void exportPrivateKey(String path);

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

}

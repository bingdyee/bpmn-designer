package io.incubator.common.security;


import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * AES
 *
 * @author :  Noa Swartz
 * @version : 1.0
 * @date : Created in 2019/5/7
 */
public class AES {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String IV_KEY = "020F7B4AE7A58B4C";

    public static void fileCrypto(String src, String dest, String key, String ivKey, int mode) throws IOException {
        if (mode != Cipher.DECRYPT_MODE && mode != Cipher.ENCRYPT_MODE) {
            throw new RuntimeException("Err Mode Code: " + mode);
        }
        File srcFile = new File(src);
        if (srcFile.exists() && srcFile.isFile()) {
            File destFile = new File(dest);
            if (destFile.getParentFile() != null && !destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
        }
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dest)) {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(mode, secKey, new IvParameterSpec(ivKey.getBytes(StandardCharsets.UTF_8)));
            if (mode == Cipher.DECRYPT_MODE) {
                CipherOutputStream cout = new CipherOutputStream(out, cipher);
                write(in, cout);
                cout.close();
            }
            if (mode == Cipher.ENCRYPT_MODE) {
                CipherInputStream cin = new CipherInputStream(in, cipher);
                write(cin, out);
                cin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String data, String key) {
        return Hex.bytes2hex(aes(data.getBytes(), key.getBytes(), Cipher.ENCRYPT_MODE));
    }

    public static String decrypt(String data, String key) {
        return new String(Objects.requireNonNull(aes(Hex.hex2bytes(data), key.getBytes(), Cipher.DECRYPT_MODE)), StandardCharsets.UTF_8);
    }

    private static byte[] aes(byte[] data, byte[] key, int mode) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_KEY.getBytes());
            cipher.init(mode, keySpec, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void write(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int nRead;
        while ((nRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, nRead);
            out.flush();
        }
    }

}
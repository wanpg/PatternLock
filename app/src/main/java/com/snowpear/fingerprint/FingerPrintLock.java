package com.snowpear.fingerprint;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

/**
 * Created by wangjinpeng on 16/6/13.
 */
public class FingerPrintLock {


    /**
     *  指纹识别专用的用于加解密keystore的name
     */
    private static final String FINGER_PRINT_CRYPT_KEY_NAME = "SNOW_PEAR_FINGER_CRYPT_KEY";
    private static final String FINGER_PRINT_SHA_KEY_NAME = "SNOW_PEAR_FINGER_SHA_KEY";

    private static FingerPrintLock instance = new FingerPrintLock();

    public static FingerPrintLock getInstance() {
        return instance;
    }

    public FingerPrintLock() {
        // 如果不存在key,创建一个
        createKey();
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    public void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if(!keyStore.containsAlias(FINGER_PRINT_CRYPT_KEY_NAME)) {
                // Set the alias of the entry in Android KeyStore where the key will appear
                // and the constrains (purposes) in the constructor of the Builder
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(new KeyGenParameterSpec.Builder(FINGER_PRINT_CRYPT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)// 需要用户授权,在每次使用此key的时候
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            }

            if(!keyStore.containsAlias(FINGER_PRINT_SHA_KEY_NAME)) {
                // Set the alias of the entry in Android KeyStore where the key will appear
                // and the constrains (purposes) in the constructor of the Builder
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512, "AndroidKeyStore");
                keyGenerator.init(new KeyGenParameterSpec.Builder(FINGER_PRINT_CRYPT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)// 需要用户授权,在每次使用此key的时候
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            }
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密,用指纹加密
     */
    public void encrypt(String src){

    }

    public void decrypt(){

    }
}

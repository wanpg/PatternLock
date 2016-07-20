package com.snowpear.patternlock;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.snowpear.common.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by wangjinpeng on 16/4/19.
 * 图案解锁的控制器
 */
final public class PatternLock {

    private final static PatternLock instance = new PatternLock();

    public static PatternLock getInstance() {
        return instance;
    }

    private PatternLock() {
//        KeyStore.getInstance("").
//        KeyInfo.isInsideSecurityHardware();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void createNewKeys(Context context, String alias, String password) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);

                AlgorithmParameterSpec parameterSpec = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    parameterSpec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setCertificateSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                            .setCertificateSerialNumber(BigInteger.ONE)
                            .setKeyValidityStart(start.getTime())
                            .setKeyValidityEnd(end.getTime())
                            .build();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    parameterSpec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                }

                KeyGenerator generator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
                generator.init(parameterSpec);

                SecretKey key = generator.generateKey();
                Utils.debug("创建的keypair私钥：" + key.getAlgorithm());
            }
        } catch (KeyStoreException | InvalidAlgorithmParameterException | NoSuchProviderException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        refreshKeys(keyStore);
    }

    private void refreshKeys(KeyStore keyStore) {
        ArrayList<String> keyAliases = new ArrayList<>();
        if(keyStore != null) {
            try {
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    keyAliases.add(aliases.nextElement());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Utils.debug("keystore---:" + keyAliases.toString());
    }

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }


}

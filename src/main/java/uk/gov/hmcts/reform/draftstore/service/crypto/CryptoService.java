package uk.gov.hmcts.reform.draftstore.service.crypto;

import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;
import uk.gov.hmcts.reform.draftstore.utils.IllegalArgument;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.common.base.Charsets.UTF_8;

public class CryptoService {

    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final Length KEY_LENGTH = Length.BITS_128;
    private static final Length IV_LENGTH = Length.BITS_96;
    private static final Length GMC_TAG_LENGTH = Length.BITS_128;

    public static byte[] encrypt(String input, String secret) {

        IllegalArgument.throwIf(() -> input == null, "Input can't be null");
        IllegalArgument.throwIf(() -> Strings.isNullOrEmpty(secret), "Secret can't be empty");

        try {
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(
                Cipher.ENCRYPT_MODE,
                deriveKey(secret),
                new GCMParameterSpec(GMC_TAG_LENGTH.bits(), generateIv())
            );

            return Bytes.concat(
                cipher.getIV(),
                cipher.doFinal(input.getBytes(UTF_8))
            );

        } catch (GeneralSecurityException exc) {
            throw new GeneralSecurityRuntimeException(exc);
        }
    }

    public static String decrypt(byte[] input, String secret) {

        IllegalArgument.throwIf(() -> input == null || input.length == 0, "Input can't be empty");
        IllegalArgument.throwIf(() -> Strings.isNullOrEmpty(secret), "Secret can't be empty");

        try {
            byte[] iv = Arrays.copyOfRange(input, 0, IV_LENGTH.bytes());
            byte[] cipherText = Arrays.copyOfRange(input, IV_LENGTH.bytes(), input.length);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(
                Cipher.DECRYPT_MODE,
                deriveKey(secret),
                new GCMParameterSpec(GMC_TAG_LENGTH.bits(), iv)
            );

            return new String(cipher.doFinal(cipherText), UTF_8);

        } catch (AEADBadTagException exc) {
            throw new InvalidKeyException("Invalid secret");
        } catch (GeneralSecurityException exc) {
            throw new GeneralSecurityRuntimeException(exc);
        }
    }

    private static SecretKey deriveKey(String secret) throws NoSuchAlgorithmException {
        byte[] hashedSecret = MessageDigest.getInstance("SHA-512").digest(secret.getBytes());
        byte[] key = Arrays.copyOfRange(hashedSecret, 0, KEY_LENGTH.bytes());

        return new SecretKeySpec(key, "AES");
    }

    private static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH.bytes()];
        new SecureRandom().nextBytes(iv);

        return iv;
    }
}

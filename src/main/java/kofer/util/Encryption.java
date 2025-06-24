package kofer.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * The Encryption class provides utility methods for securely encrypting
 * and decrypting objects to and from files using the AES-GCM encryption
 * algorithm with password-based key generation.
 * The class includes methods to:
 * - Generate a cryptographically secure salt.
 * - Generate AES keys from a password and salt using the PBKDF2 algorithm.
 * - Encrypt an object to a file using AES-GCM.
 * - Decrypt an object from a file using AES-GCM.
 * It utilizes the following cryptographic principles:
 * - AES-GCM for authenticated encryption to ensure both confidentiality
 *   and integrity of data.
 * - PBKDF2 for deriving a secure key from a password.
 * Note:
 * - A unique salt and initialization vector (IV) are generated for each
 *   encryption and stored in the file alongside the encrypted data.
 * - Ensure the password used for encryption and decryption remains secure
 *   to maintain data confidentiality.
 *
 * The functionality provided by this class is vital for securely persisting
 * sensitive data, such as transaction records or other user data.
 */
public class Encryption {

    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int SALT_SIZE = 16;
    private static final int TAG_LENGTH = 128;
    private static final int ITERATIONS = 65536;
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public static SecretKey getKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static void encryptToFile(Object data, String password, File outputFile) throws Exception {
        byte[] salt = generateSalt();
        SecretKey key = getKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher);
             ObjectOutputStream oos = new ObjectOutputStream(cos)) {

            fos.write(salt);
            fos.write(iv);
            oos.writeObject(data);
        }catch (Exception e){
            System.err.println("Failed to encrypt data: " + e.getMessage());
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    public static Object decryptFromFile(String password, File inputFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] salt = fis.readNBytes(SALT_SIZE);
            byte[] iv = fis.readNBytes(IV_SIZE);
            SecretKey key = getKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            try (CipherInputStream cis = new CipherInputStream(fis, cipher);
                 ObjectInputStream ois = new ObjectInputStream(cis)) {
                return ois.readObject();
            } catch (Exception e) {
                System.err.println("Failed to decrypt data: " + e.getMessage());
                return null;
            }
        }
    }
}

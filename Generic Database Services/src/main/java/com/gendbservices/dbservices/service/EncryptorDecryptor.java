package com.gendbservices.dbservices.service;

import com.gendbservices.dbservices.configuration.DatabaseServiceConstants;
import com.gendbservices.dbservices.exception.GenDbException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class EncryptorDecryptor {

    private static final Logger logger = LoggerFactory.getLogger(EncryptorDecryptor.class);
    private Cipher cipher;
    byte[] encKeyBytes;
    private SecretKeySpec secretKeySpec;

    public EncryptorDecryptor() throws GenDbException{

        MessageDigest messageDigest = null;
        try {
            logger.info("Creating cipher and secrete encryption key ");
            byte[] key = DatabaseServiceConstants.ENCRYPTION_KEY.getBytes(DatabaseServiceConstants.UNICODE_FORMAT);
            messageDigest = MessageDigest.getInstance(DatabaseServiceConstants.SHA_ONE);
            key = messageDigest.digest(key);

            key = Arrays.copyOf(key, DatabaseServiceConstants.NO_OF_BIT_ENC);
            secretKeySpec = new SecretKeySpec(key, DatabaseServiceConstants.ENCRYPTION_ALGO);
            cipher = Cipher.getInstance(DatabaseServiceConstants.TRANSFORMATION_TYPE);

        }catch (NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException genericException){
            logger.info("Error while creating cipher/encryption key cause: {}, message: {}", genericException.getCause(), genericException.getMessage());
            throw new GenDbException(genericException.getMessage(), genericException.getCause());
        }
    }

    public String aesEncrypt(String aesEncryption) throws GenDbException {

        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(aesEncryption.getBytes(DatabaseServiceConstants.UNICODE_FORMAT)));

        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new GenDbException(ex.getMessage(), ex.getCause());
        }
        return encryptedString;
    }

    public String aesDecrypt(String aesDecryption) throws GenDbException {

        String decryptedString = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryptedString = new String(cipher.doFinal(Base64.decodeBase64(aesDecryption)));

        } catch (InvalidKeyException |  IllegalBlockSizeException | BadPaddingException ex) {
            ex.printStackTrace();
            throw new GenDbException(ex.getMessage(), ex.getCause());
        }
        return decryptedString;
    }
}

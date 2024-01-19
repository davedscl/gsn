package ch.epfl.gsn.ac;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 14, 2010
 * Time: 4:39:13 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import sun.misc.*;

import java.util.Base64;

/* This class helps to encrypt user posswords using AES encryption algo, we use a salt for a more robust encryption, salt is stored in a property file "acuserpassword.properties"*/
public class Protector
{
    private static transient Logger logger                             = LoggerFactory.getLogger( Protector.class );

    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 2;


    private static final byte[] keyValue =new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        String salt=getSalt();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);

        String valueToEnc = null;
        String eValue = value;
        for (int i = 0; i < ITERATIONS; i++)
        {
            valueToEnc = salt + eValue;
            byte[] encValue = c.doFinal(valueToEnc.getBytes());
            //eValue = new BASE64Encoder().encode(encValue);
           
            eValue = Base64.getEncoder().encodeToString(encValue); // change to Base64 since might cause compatibility issues or unexpected behavior when upgrading to newer Java versions.
        }
        return eValue;
    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        String salt=getSalt();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);

        String dValue = null;
        String valueToDecrypt = value;
        for (int i = 0; i < ITERATIONS; i++)
        {
            //byte[] decordedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);
            byte[] decordedValue = Base64.getDecoder().decode(valueToDecrypt); // same here
            byte[] decValue = c.doFinal(decordedValue);
            dValue = new String(decValue).substring(salt.length());
            valueToDecrypt = dValue;
        }
        return dValue;
    }
    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
    private static String getSalt()
    {
        
        return System.getProperty("salt");
       
    }
}


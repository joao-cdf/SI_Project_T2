package t2_project;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import static t2_project.Lib.getSalt;


public class AES_Decrypter {
    
    private Cipher oEncCipher;
    private Cipher oDecCipher;
    private byte[] salt;   
    private final String password;
    private final int iterationCount = 1024;
    private final int keyStrength = 256;
    private SecretKey key;
    private PBEKeySpec spec;
    private SecretKeyFactory factory;
    private SecretKeySpec secret;
    private byte[] iv;
    
    public void generateSalt() throws NoSuchAlgorithmException  {        
        this.salt = getSalt();        
    }
    
    public void generateIV() throws Exception   {
        AlgorithmParameters params;
        
        oEncCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        oEncCipher.init(Cipher.ENCRYPT_MODE, secret);
        params = oEncCipher.getParameters();
        
        iv = params.getParameterSpec(IvParameterSpec.class).getIV();        
    }
    
    private void generateKey() throws Exception {
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        spec = new PBEKeySpec(this.password.toCharArray(), this.salt,this.iterationCount, this.keyStrength);
        key = factory.generateSecret(spec);
        secret = new SecretKeySpec(key.getEncoded(), "AES");
    }

    public byte[] getSaltVal()  {
        return this.salt;
    }
    
    public byte[] getIv()  {
        return this.iv;
    } 
    
    public byte[] encrypt(byte[] data) throws Exception {        
        byte[] utf8EncryptedData = oEncCipher.doFinal(data);        
        return utf8EncryptedData;
    }
    
    public byte[] decrypt(byte[] data) throws Exception {
        oDecCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        oDecCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));        
        byte[] decrypted = oDecCipher.doFinal(data);
        return decrypted;
    }
    
    AES_Decrypter(String password) throws Exception   {
        
        this.password = password;
        
        generateSalt();
        generateKey();
        generateIV();
        
    }
    
    AES_Decrypter(String password, byte[] salt, byte[] iv) throws Exception   {        
        
        this.password = password;
        this.salt = salt;
        generateKey();
        oEncCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        oEncCipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));  
        this.iv = iv;
    }
    
    
    
    
}

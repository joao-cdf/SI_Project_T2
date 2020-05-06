package t2_project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Lib {
    
        
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xff;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0f];
        }
    return new String(hexChars);
    }
    
    public static byte[] hexToBytes(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}
    
    public static String getSHA(String input) throws NoSuchAlgorithmException  {
        try {
            
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
                byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                BigInteger no = new BigInteger (1, encodedhash); 
                String hashText = no.toString(16);
                while(hashText.length() < 32)   {
                    hashText = "0" + hashText;
                }
                
                return hashText;
        } catch (NoSuchAlgorithmException e){
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        return null;
    }
    
    public static byte[] getSalt() throws NoSuchAlgorithmException  {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[32];
        sr.nextBytes(salt);
        return salt;
    }
        
    public static String fileHash(String filename) throws NoSuchAlgorithmException, FileNotFoundException, IOException   {
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try {
            FileInputStream fis = new FileInputStream(filename);
            byte[] dataBytes = new byte[1024];
     
            int nread = 0; 
            while ((nread = fis.read(dataBytes)) != -1) 
                md.update(dataBytes, 0, nread);
        }catch (FileNotFoundException e)                         {
            System.out.println ("File not found...");            
        }
        byte[] mdbytes = md.digest();
     
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}

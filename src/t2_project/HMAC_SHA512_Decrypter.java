package t2_project;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HMAC_SHA512_Decrypter {
    
    public static byte[] calculateHMAC(byte[] data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        
        SecretKeySpec secretKeySpec= new SecretKeySpec(key.getBytes(), "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
        
    }
    
    
}

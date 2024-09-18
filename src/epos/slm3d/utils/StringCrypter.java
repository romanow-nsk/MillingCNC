package epos.slm3d.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;


public class StringCrypter {
    private final static class MySecretKey implements SecretKey {
        private byte[] key = new byte[]{3,1,4,1,5,9,2,6};
        // ключ не должен иметь длину более 8 байт, для безопасного шифрования его необходимо изменить
        public String getAlgorithm() { return "DES"; }
        public String getFormat() { return "RAW"; }
        public byte[] getEncoded() { return key; }
        }


    public static String encrypt(String str) {
        try {
            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, new MySecretKey());
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return new sun.misc.BASE64Encoder().encode(enc);
            } catch (Exception ex) {}
        return null;
        }

    public static String decrypt(String str)  {
        try {
            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.DECRYPT_MODE, new MySecretKey());
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            byte[] utf8 = ecipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (Exception ex) {}
        return null;
        }
    public static void main(String argv[]){
        String zz = StringCrypter.encrypt("");
        String tt = StringCrypter.decrypt(zz);
        System.out.println(zz+" "+tt);
    }
}

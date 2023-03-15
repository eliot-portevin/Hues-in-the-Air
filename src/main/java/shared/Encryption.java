package shared;

/**
 * Called by client and server to encrypt and decrypt messages.
 * */
public class Encryption {
    /**
     * Takes in a message and encrypts it.
     * */
    public static String encrypt(String message) {
        int key = 5;
        char[] chars = message.toCharArray();
        int[] arr = new int[chars.length];
        for(int i=0;i<chars.length;i++) {
            arr[i] = (int) chars[i];
            arr[i] += key;
            chars[i] = (char) arr[i];
        }
        message = "";
        for (char c : chars){
            message += c;
        }
        return message;
    }
    /**
     * Takes in a message and decrypts it.
     * */
    public static String decrypt(String message) {
        int key = 5;
        char[] chars = message.toCharArray();
        int[] arr = new int[chars.length];
        for(int i=0;i<chars.length;i++) {
            arr[i] = (int) chars[i];
            arr[i] -= key;
            chars[i] = (char) arr[i];
        }
        message = "";
        for (char c : chars){
            message += c;
        }
        return message;
    }
}

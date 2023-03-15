package shared;

/**
 * Called by client and server to encrypt and decrypt messages.
 * */
public class Encryption {
    /**
     * Takes in a message and encrypts it.
     * */
    public static String encrypt(String message) {
        int key = 100; // key must be equal on server and client
        char[] chars = message.toCharArray();
        int[] arr = new int[chars.length];
        /**
         * Add key value to char value and make sure to properly loop
         */
        for (int i=0;i<chars.length;i++) {
            arr[i] = chars[i];
            if (arr[i] + key > 127){
                arr[i] = (arr[i] + key)%127;
                chars[i] = (char) arr[i];
            }
            else {
                arr[i] += key;
                chars[i] = (char) arr[i];
            }
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (char c : chars){
            messageBuilder.append(c);
        }
        message = messageBuilder.toString();
        return message;
    }
    /**
     * Takes in a message and decrypts it.
     * */
    public static String decrypt(String message) {
        int key = 100;
        char[] chars = message.toCharArray();
        int[] arr = new int[chars.length];
        /**
         * Subtracts key from char value and makes sure it's properly looped
         */
        for (int i=0;i<chars.length;i++) {
            arr[i] = chars[i];
            if (arr[i] - key < 0){
                arr[i] = (1270000+((arr[i] - key)))%127;
                chars[i] = (char) arr[i];
            }
            else {
                arr[i] -= key;
                chars[i] = (char) arr[i];
            }
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (char c : chars){
            messageBuilder.append(c);
        }
        message = messageBuilder.toString();
        return message;
    }
}

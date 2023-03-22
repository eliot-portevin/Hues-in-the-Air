package shared;

/** Called by client and server to encrypt and decrypt messages. */
public class Encryption {

  public static String encrypt(String message) {
    return shifter(message, 5);
  }

  public static String decrypt(String message) {
    return shifter(message, -5);
  }

  /**
   * Iterates through the message and shifts each character by the shift value. This rudimentary
   * encryption method is known as the Caesar cipher (see
   * https://en.wikipedia.org/wiki/Caesar_cipher).
   *
   * @param message
   * @param shift
   * @return
   */
  public static String shifter(String message, int shift) {
    if (message == null) {
      return null;
    }
    char[] chars = message.toCharArray();

    for (int i = 0; i < chars.length; i++) {
      chars[i] = (char) ((chars[i] + shift + 127) % 127);
    }
    return new String(chars);
  }
}

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.Math;

/**
 * Created by Jose Rogado on 05-11-2018.
 */

/* Could use Fast MD5... but is not so fast as Java native MD5
import com.twmacinta.io.*;
import com.twmacinta.util.*;
*/

public class md5CrackSerial {
    // passHash = new byte[]{(byte)0xb3, 0x73, (byte)0x87, 0x0b, (byte)0x91, 0x39, (byte)0xbb, (byte)0xad, (byte)0xe8, 0x33, (byte)0x96, (byte)0xa4, (byte)0x9b, 0x1a, (byte)0xfc, (byte)0x9a};

    private static byte[] passHash;
    // Number of symbols in password
    private static final int PASSLEN = 6;
    // Possible password symbols
    private static String symbols = "abcdefghijklmnopqrstuvwxyz*#";
    // Byte Symbols
    private static byte[] byteSymbols;
    // Total number of possible passwords with PASSLEN length and symbols.length
    private static long nPasswords;

    // Power digits using the number of symbols
    private static int[] digitPowers;
    private static byte[] password;

    // MD5 md;
    private MessageDigest md;

    private md5CrackSerial(String passHashString)  throws NoSuchAlgorithmException {
        int i;

        // The hash string to crack
        passHash = new byte[passHashString.length() / 2];
        // Convert passHashString to bytes
        for (i = 0; i < passHashString.length() / 2; i++) {
            byte high = (byte)(xtob(passHashString.charAt(2 * i)) << 4);
            byte low = xtob(passHashString.charAt(2 * i + 1));
            passHash[i] = (byte) (high + low);
            // System.out.printf("%02x ", passHash[i]);
        }
        digitPowers = new int[PASSLEN + 1];
        System.out.print("\nDigit Powers: ");
        for (i = 0; i < PASSLEN + 1; i++) {
            digitPowers[i] = (int) Math.pow(symbols.length(), i);
            System.out.print(digitPowers[i] + " ");
        }
        byteSymbols = symbols.getBytes();
        nPasswords = (long) Math.pow(symbols.length(), PASSLEN);
        password = new byte[PASSLEN];
        /*md = new MD5();*/
        md = MessageDigest.getInstance("MD5");
    }

    private void hashExplore() {
        int i, n, loops = 0, digit;
        try {
            for (n = 0; n < nPasswords; n++) {
                for (i = PASSLEN - 1; i >= 0; i--) {
                    digit = (n % digitPowers[i + 1]) / digitPowers[i];
                    password[PASSLEN - 1 - i] = byteSymbols[digit];
                }
                // password[PASSLEN] =  0; // No need for trailing zero!
                if (n % digitPowers[PASSLEN - 1] == 0) { // Debug
                    System.out.printf("%6d ", n);
                    printHash(password, password.length, "%c", true);
                }
                loops++;
                if (testPass(password) == 0) {
                    System.out.print("Found password: ");
                    printHash(password, password.length, "%c", false);
                    System.out.printf(" after %d iterations\n", loops);
                    break;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such Algorithm");
        }
    }

    private int testPass(byte[] password) throws NoSuchAlgorithmException {

        // MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(password);
        byte[] hash = md.digest();
/*
        md = new MD5();
        // md.Init();
        md.Update(password);
        byte[] hash = md.Final();
*/
/*
        if (n % power[PASSLEN-1] == 0) {
            System.out.printf("length = %d ",password.length);
            printHash(password, password.length, "%c",true);
        }
*/
        int diff;
        int i = 0;
        // Compare hashes
        while (((diff = passHash[i] ^ hash[i]) == 0) && (i < hash.length-1))
            i++;
//        int diff = passHash[0] ^ hash[0];
//        for (i = 1; i < hash.length; i++) { // Testing all the bytes avoids password guessing
//            diff |= passHash[i] ^ hash[i];
//        }
        if (diff == 0) {
            System.out.print("\nMatch ");
        }
        return diff;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String passHashString = "4e510be093d346512011c3f4fe36e4af";
        if (args.length == 1){
            passHashString = args[0];
        }
        md5CrackSerial crackIt = new md5CrackSerial(passHashString);

        System.out.print("\nCracking Hash: ");
        printHash(passHash, passHash.length, "%02x", true);
        System.out.print("\nSearching password of " + PASSLEN + " characters using " + symbols.length() + " symbols: ");
        printHash(byteSymbols, byteSymbols.length, "%c", true);

        long startTime = System.currentTimeMillis();
        crackIt.hashExplore();
        long stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Serial Time Elapsed: " + (float) serialTime / 1000.0 + " seconds");

    }

    private static void printHash(byte[] hash, int length, String format, boolean newline) {
        for (int i = 0; i < length; i++)
            System.out.printf(format, hash[i]);
        if (newline)
            System.out.println();
    }

    // Converts ascii hexadecimal to byte
    private static byte xtob(char c) {
        byte result;

        switch (c) {
            case 'f':
                result = 15;
                break;
            case 'e':
                result = 14;
                break;
            case 'd':
                result = 13;
                break;
            case 'c':
                result = 12;
                break;
            case 'b':
                result = 11;
                break;
            case 'a':
                result = 10;
                break;
            case '9':
                result = 9;
                break;
            case '8':
                result = 8;
                break;
            case '7':
                result = 7;
                break;
            case '6':
                result = 6;
                break;
            case '5':
                result = 5;
                break;
            case '4':
                result = 4;
                break;
            case '3':
                result = 3;
                break;
            case '2':
                result = 2;
                break;
            case '1':
                result = 1;
                break;
            case '0':
                result = 0;
            default:
                result = 0;
        }
        return result;
    }
}

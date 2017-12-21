package eu.securityproject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import eu.securityproject.utils.Base32;

/**
 * Created by Letizia Vitari on 20/12/17.
 */
public class OTPManagement {

    static final int digit = 8;
    static final int period = 20;

    public static Boolean isValidOtp(String otp, String secret) throws NoSuchAlgorithmException, InvalidKeyException {

        //Controllo se e' un numero
        Long otpLong = null;
        try {
            otpLong = Long.parseLong(otp);
        }catch (NumberFormatException e) {
            return Boolean.FALSE;
        }

        //Controllo se e' della lunghezza corretta
        if(otp.length() != digit) return Boolean.FALSE;

        //Controllo se un codice ancora valido
        if(!checkCode(Base32.decode(secret), otpLong.longValue())) return Boolean.FALSE;

        return Boolean.TRUE;
    }



    private static final int windowSizeSteps = 1;

    private static long getTimeWindowFromTime(long time, int period)
    {
        return time / TimeUnit.SECONDS.toMillis(period);
    }

    private static boolean checkCode(byte[] decodedKey, long otpCode) throws InvalidKeyException, NoSuchAlgorithmException
    {
        Date now = new Date();
        long timestamp = now.getTime();

        for (int i = - windowSizeSteps; i < windowSizeSteps+1 ; ++i)
        {
            Calendar windowsCalendar = Calendar.getInstance();
            windowsCalendar.setTimeInMillis(timestamp);
            windowsCalendar.add(Calendar.SECOND, i*period);

            final long timeWindow = getTimeWindowFromTime(windowsCalendar.getTimeInMillis(), period);

            long hash = calculateCode(decodedKey, timeWindow, digit );
            if (hash == otpCode)
            {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    static int calculateCode(byte[] key, long tm, int codeDigits) throws NoSuchAlgorithmException, InvalidKeyException
    {
        // Allocating an array of bytes to represent the specified instant
        // of time.
        byte[] data = new byte[8];
        long value = tm;

        // Converting the instant of time from the long representation to a
        // big-endian array of bytes (RFC4226, 5.2. Description).
        for (int i = 8; i-- > 0; value >>>= 8)
        {
            data[i] = (byte) value;
        }

        // Building the secret key specification for the HmacSHA1 algorithm.
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA256");

        // Getting an HmacSHA1/HmacSHA256 algorithm implementation from the JCE.
        Mac mac = Mac.getInstance("HmacSHA256");

        // Initializing the MAC algorithm.
        mac.init(signKey);

        // Processing the instant of time and getting the encrypted data.
        byte[] hash = mac.doFinal(data);

        // Building the validation code performing dynamic truncation
        // (RFC4226, 5.3. Generating an HOTP value)
        int offset = hash[hash.length - 1] & 0xF;

        // We are using a long because Java hasn't got an unsigned integer type
        // and we need 32 unsigned bits).
        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i)
        {
            truncatedHash <<= 8;

            // Java bytes are signed but we need an unsigned integer:
            // cleaning off all but the LSB.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        // Clean bits higher than the 32nd (inclusive) and calculate the
        // module with the maximum validation code value.
        truncatedHash &= 0x7FFFFFFF;
        int keyModulus = (int) Math.pow(10, codeDigits);
        truncatedHash %= keyModulus;

        // Returning the validation code to the caller.
        return (int) truncatedHash;
    }

}

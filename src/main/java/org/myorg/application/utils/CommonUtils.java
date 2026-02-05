package org.myorg.application.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommonUtils {

    private static final Logger LOGGER =
            Logger.getLogger(CommonUtils.class.getName());

    public static String generateApiKey() {
        return UUID.randomUUID().toString();
    }

    public static boolean isValidEmail(String string) {
        // Regular expression to match valid email formats
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // Compile the regex
        Pattern p = Pattern.compile(emailRegex);

        return string != null && p.matcher(string).matches()
                && string.length() <= 128;
    }

    public static String formatPhoneNumber(String string) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber
                    proto = phoneUtil.parse(string, "DO");
            return phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            return string;
        }
    }

    public static boolean isValidPhoneNumber(String string) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            return phoneUtil.isValidNumber(phoneUtil.parse(string, "DO"));
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static String capitalizeEachWord(String input) {

        if(input == null)
            return "";
        return Arrays.stream(input.split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            URI uri = url.toURI();
            // Optional: add further checks for specific schemes, hosts, etc. if needed
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static boolean generateBoolean50PercentTrue() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}

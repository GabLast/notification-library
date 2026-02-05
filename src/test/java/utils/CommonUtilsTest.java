package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.utils.CommonUtils;

public class CommonUtilsTest {

    @Test
    void generateApiKey() {
        String token = CommonUtils.generateApiKey();
        Assertions.assertNotNull(token);
        Assertions.assertNotSame("", token);
    }

    @Test
    void isValidEmail() {
        Assertions.assertTrue(CommonUtils.isValidEmail("mail@mail.com"));
        Assertions.assertFalse(CommonUtils.isValidEmail("mail"));
        Assertions.assertFalse(CommonUtils.isValidEmail(null));
    }

    @Test
    void formatPhoneNumber() {
        Assertions.assertTrue(CommonUtils.formatPhoneNumber("+18092022012")
                .equalsIgnoreCase("+1 809-202-2012"));

        Assertions.assertTrue(CommonUtils.formatPhoneNumber("a").equalsIgnoreCase("a"));
    }

    @Test
    void isValidPhoneNumber() {
        Assertions.assertFalse(CommonUtils.isValidPhoneNumber("a"));
        Assertions.assertTrue(CommonUtils.isValidPhoneNumber("+18092022012"));
    }

    @Test
    void capitalizeEachWord() {
        Assertions.assertTrue(
                CommonUtils.capitalizeEachWord("hola hola").equals("Hola Hola"));

        Assertions.assertTrue(CommonUtils.capitalizeEachWord("a").equals("A"));

        Assertions.assertTrue(CommonUtils.capitalizeEachWord("aA").equals("AA"));

        Assertions.assertFalse(
                CommonUtils.capitalizeEachWord("marco polo").equals("marco Polo"));
    }

    @Test
    void isValidUrl() {
        Assertions.assertTrue(CommonUtils.isValidUrl("https://www.youtube.com/"));
        Assertions.assertFalse(CommonUtils.isValidUrl("not an url"));
        Assertions.assertFalse(CommonUtils.isValidUrl(null));
    }

    @Test
    void generateBoolean50PercentTrue() {
        Assertions.assertDoesNotThrow(() -> {
            CommonUtils.generateBoolean50PercentTrue();
        });
    }
}

package payments.duo.utils;

public class Constants {
    public static final String VALID_USERNAME_LENGTH_MESSAGE = "must be between 4 and 16 symbols length";
    public static final String VALID_USERNAME_PATTERN_MESSAGE = "must consists of alphanumeric characters only," +
            " no special characters or white spaces";
    public static final String VALID_USERNAME_REGEX_PATTERN = "^[a-zA-Z0-9]{4,16}";
    public static final String VALID_PASSWORD_LENGTH_MESSAGE = "must be min 6 symbols length";
}
package payments.duo.utils;
//TODO move to file
public class Constants {
    public static final String VALID_USERNAME_LENGTH_MESSAGE = "must be between 4 and 16 symbols length";
    public static final String VALID_USERNAME_PATTERN_MESSAGE = "must consists of alphanumeric characters only," +
            " no special characters or white spaces";
    public static final String VALID_USERNAME_REGEX_PATTERN = "^[a-zA-Z0-9]{4,16}";
    public static final String VALID_PASSWORD_LENGTH_MESSAGE = "must be min 6 symbols length";
    public static final String VALID_NOT_BLANK_MESSAGE = "must not be blank";
    public static final String VALID_MAIL_FORMAT_MESSAGE = "must be a well-formed email address";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_NOT_FOUND = "Token has not been found";
    public static final String TOKEN_DECLARATION_IS_WRONG = "Token declaration is wrong";
    public static final String TOKEN_IS_EXPIRED = "The Token has expired";
    public static final String VALID_PAYMENT_AMOUNT_MESSAGE = "must be a positive value";
    public static final String USER_NOT_FOUND_MESSAGE_ID = "User with username: %d hadn't been found";
    public static final String USER_NOT_FOUND_MESSAGE_USERNAME = "Payment with id: %s hasn't been found";
    public static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment with id: %d hasn't been found";
    public static final String CATEGORY_NOT_FOUND_MESSAGE = "Payment with id: %d hasn't been found";
}
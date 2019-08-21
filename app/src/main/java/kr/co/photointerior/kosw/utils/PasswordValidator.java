package kr.co.photointerior.kosw.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 패스워드 유효성 검증 클래스.
 * Created by kugie on 2018. 04. 30.
 */
public class PasswordValidator {
    private static PasswordValidator INSTANCE = new PasswordValidator();
    private static String pattern = null;

    private PasswordValidator() {
        //do nothing
    }

    /**
     *
     * @param forceSpecialChar 특수문자 포함
     * @param forceCapitalLetter 대문자 포함
     * @param forceNumber 숫자 포함
     * @param minLength 최소길이
     * @param maxLength 최대길이
     * @return 이 클래스의 instance
     */
    public static PasswordValidator instance(boolean forceSpecialChar,
                                                   boolean forceCapitalLetter,
                                                   boolean forceNumber,
                                                   int minLength,
                                                   int maxLength) {
        StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");

        if (forceSpecialChar) {
            patternBuilder.append("(?=.*[@#$%])");
        }

        if (forceCapitalLetter) {
            patternBuilder.append("(?=.*[A-Z])");
        }

        if (forceNumber) {
            patternBuilder.append("(?=.*d)");
        }

        patternBuilder.append(".{" + minLength + "," + maxLength + "})");
        pattern = patternBuilder.toString();

        return INSTANCE;
    }

    /**
     * 패스워드 유효성 검증
     *
     * @param password
     * @return
     */
    public boolean validatePassword(final String password) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}

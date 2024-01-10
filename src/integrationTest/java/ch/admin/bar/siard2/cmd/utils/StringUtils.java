package ch.admin.bar.siard2.cmd.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String trim(final String text, final int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength);
        }
    }
}

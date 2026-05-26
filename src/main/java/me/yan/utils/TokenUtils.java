package me.yan.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * token工具类
 * @Author: 冯德衍
 */
@Component
public class TokenUtils {

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[a-zA-Z]+");

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return tokens;
        }
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}

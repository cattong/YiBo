package com.shejiaomao.weibo.service.listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.MultiAutoCompleteTextView.Tokenizer;

public class EditMicroBlogTokenizer implements Tokenizer {
	private static final Pattern MENTION_PATTERN = Pattern.compile("@[\\w-]+");

	public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && text.charAt(i - 1) != '@') {
        	i--;
        }
        if (i > 0 && i < cursor) {
        	CharSequence mention = text.subSequence(i - 1, cursor);
        	Matcher matcher = MENTION_PATTERN.matcher(mention);
        	if (!matcher.matches()) {
        		i = cursor;
        	}
        } else {
        	i = cursor;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == ' ') {
                return i;
            } else {
                i++;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == '@') {
            return text;
        } else {
            return text + " ";
        }
    }
}

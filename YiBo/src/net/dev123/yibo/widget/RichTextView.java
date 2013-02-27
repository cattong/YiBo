package net.dev123.yibo.widget;

import java.util.regex.Pattern;

import net.dev123.commons.ServiceProvider;
import net.dev123.mblog.FeaturePatternUtils;
import net.dev123.yibo.common.Constants;
import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class RichTextView extends TextView {

	private int off; //字符串的偏移值
	private String[] highlightArray;
	//平台
	private ServiceProvider provider = ServiceProvider.Sina;

	public RichTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RichTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean getDefaultEditable() {
	    return false;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {

	    Spannable s;
        if (text instanceof Spannable) {
            s = (Spannable) text;
        } else {
        	if (text == null) {
        		text = "";
        	}
            s = Spannable.Factory.getInstance().newSpannable(text);
        }

        //email
        Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);

        //metion
        Pattern mentionPattern = FeaturePatternUtils.getMentionPattern(provider);
        if (mentionPattern != null) {
        	Linkify.addLinks(s, mentionPattern, Constants.URI_PERSONAL_INFO.toString());
        }

        //topic
        Pattern topicPattern = FeaturePatternUtils.getTopicPattern(provider);
        if (topicPattern != null) {
            Linkify.addLinks(s, topicPattern, Constants.URI_TOPIC.toString());
        }

        //url
        Pattern urlPattern = FeaturePatternUtils.getUrlPattern(provider);
        if (urlPattern != null) {
        	Linkify.addLinks(s, urlPattern, "http://");
        	URLSpan[] urlSpans = s.getSpans(0, s.length(), URLSpan.class);
        	YiBoUrlSpan yiboSpan = null;
        	for(URLSpan urlSpan : urlSpans) {
        		yiboSpan = new YiBoUrlSpan(urlSpan.getURL());
        		int start = s.getSpanStart(urlSpan);
        		int end = s.getSpanEnd(urlSpan);
        		s.removeSpan(urlSpan);
        		s.setSpan(yiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        	}
    		if (Constants.DEBUG && yiboSpan != null) {
    			Log.d(TAG, "change to yiboSpan");
    		}
        }

        text = s;

        super.setText(text, type);

        if (this.getLinksClickable()) {
        	setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

	public ServiceProvider getProvider() {
		return provider;
	}

	public void setProvider(ServiceProvider provider) {
		this.provider = provider;
	}

	private final static String TAG = "RichTextView";
}

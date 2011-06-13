package com.arthurdo.parser.parser;

import com.arthurdo.parser.HtmlException;
import com.arthurdo.parser.HtmlStreamTokenizer;
import com.arthurdo.parser.HtmlTag;

public class TokenWotIsATag extends Token {

    private boolean _empty;
    private String _value;
    private boolean _invalid;

    public TokenWotIsATag(HtmlStreamTokenizer tok) {
        try
        {
            HtmlTag tag = new HtmlTag();
            tok.parseTag(tok.getStringValue(), tag);
            if (tag.getTagType() == HtmlTag.T_UNKNOWN)
                throw new HtmlException("this will get caught down below");
            if (tag.isEmpty()) {
                _empty = true;
            }
            _value = tag.toString();
            _invalid = false;
        }
        catch (HtmlException e)
        {
            _invalid = true;
            _value = "<" + tok.getStringValue() + ">";
        }
    }

    public boolean isEmpty() {
        return _empty;
    }

    public String getValue() {
        return _value;
    }

    public boolean isInvalid() {
        return _invalid;
    }

}

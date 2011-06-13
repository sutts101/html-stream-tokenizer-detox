package com.arthurdo.parser.parser;

public class TokenWotIsNotATag extends Token {

    final private String _value;

    public TokenWotIsNotATag(int type, String value) {
        super(type);
        this._value = value;
    }

    @Override
    public String getValue() {
        return _value;
    }
}

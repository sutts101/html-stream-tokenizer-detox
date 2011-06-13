package com.arthurdo.parser.parser;

public class TokenWotIsNotATag extends Token {

    final private String _value;

    public TokenWotIsNotATag(String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}

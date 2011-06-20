package com.arthurdo.parser.helper;

public class OtherToken extends Token {

    final private String _value;

    public OtherToken(int type, String value) {
        super(type);
        this._value = value;
    }

    @Override
    public String getValue() {
        return _value;
    }
}

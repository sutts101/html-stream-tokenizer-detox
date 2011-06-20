package com.arthurdo.parser.helper;

import com.arthurdo.parser.HtmlStreamTokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

abstract public class Token {

    final private int _type;

    protected Token(int _type) {
        this._type = _type;
    }

    final public int getType() {
        return _type;
    }

    abstract public String getValue();
}

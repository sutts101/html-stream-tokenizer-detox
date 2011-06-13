package com.arthurdo.parser.parser;

import com.arthurdo.parser.HtmlStreamTokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

abstract public class Token {

    static public List<Token> collectTokens(String html) {
        try {
            List<Token> tokens = new ArrayList<Token>();
            HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new StringReader(html));
            while (tokenizer.nextToken() != HtmlStreamTokenizer.TT_EOF) {
                if (tokenizer.getTokenType() == HtmlStreamTokenizer.TT_TAG)
                {
                    tokens.add(new TokenWotIsATag(tokenizer));
                }
                else
                {
                    tokens.add(new TokenWotIsNotATag(tokenizer.getStringValue().toString()));
                }
            }
            return tokens;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

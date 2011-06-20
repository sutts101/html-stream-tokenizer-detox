package com.arthurdo.parser.helper;

import com.arthurdo.parser.HtmlStreamTokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TokenHelper {

    static public List<Token> collectTokens(String html) {
        try {
            List<Token> tokens = new ArrayList<Token>();
            HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new StringReader(html));
            while (tokenizer.nextToken() != HtmlStreamTokenizer.TT_EOF) {
                int type = tokenizer.getTokenType();
                if (type == HtmlStreamTokenizer.TT_TAG)
                {
                    tokens.add(new ElementToken(type, tokenizer));
                }
                else
                {
                    tokens.add(new OtherToken(type, tokenizer.getStringValue().toString()));
                }
            }
            return tokens;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

package com.arthurdo.parser.parser;

import org.junit.Test;

import java.io.*;
import java.util.List;

import static junit.framework.Assert.*;

public class ParserTests_LessCoarseGrained {

    @Test
    public void shouldHandleStartElement() throws IOException {
        Token token = parseAndReturnOneToken("<p>", 0);
        expectElementToken(token, "<p>", false, false);
    }

    @Test
    public void shouldHandleEndElement() throws IOException {
        Token token = parseAndReturnOneToken("</p>", 0);
        expectElementToken(token, "</p>", false, false);
    }

    @Test
    public void shouldHandleText() throws IOException {
        Token token = parseAndReturnOneToken("hallo world", 0);
        expectNonElementToken(token, "hallo world");
    }

    @Test
    public void shouldHandleSimpleParagraph() throws IOException {
        String html = "<p>Hallo world</p>";
        List<Token> tokens = parse(html);
        assertEquals(3, tokens.size());
    }

    private void expectElementToken(Token token, String value, boolean empty, boolean invalid) {
        TokenWotIsATag elementToken = (TokenWotIsATag) token;
        assertEquals(value, elementToken.getValue());
        assertEquals(empty, elementToken.isEmpty());
        assertEquals(invalid, elementToken.isInvalid());
    }

    private void expectNonElementToken(Token token, String value) {
        TokenWotIsNotATag elementToken = (TokenWotIsNotATag) token;
        assertEquals(value, elementToken.getValue());
    }

    private Token parseAndReturnOneToken(String htmlSnippet, int index) {
        return parse(htmlSnippet).get(index);
    }

    private List<Token> parse(String htmlSnippet) {
        return TokenHelper.collectTokens(htmlSnippet);
    }
}

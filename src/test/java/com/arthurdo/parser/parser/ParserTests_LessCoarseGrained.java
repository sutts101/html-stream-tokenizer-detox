package com.arthurdo.parser.parser;

import com.arthurdo.parser.HtmlStreamTokenizer;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static junit.framework.Assert.*;

public class ParserTests_LessCoarseGrained {

    @Test
    public void shouldHandleStartElement() {
        Token token = parseAndReturnOneToken("<p>", 0);
        expectElementToken(token, "<p>", false, false);
    }

    @Test
    public void shouldHandleEndElement() {
        Token token = parseAndReturnOneToken("</p>", 0);
        expectElementToken(token, "</p>", false, false);
    }

    @Test
    public void shouldHandleText() {
        Token token = parseAndReturnOneToken("hallo world", 0);
        expectNonElementToken(token, HtmlStreamTokenizer.TT_TEXT, "hallo world");
    }

    @Test
    public void shouldHandleSimpleParagraph() {
        String html = "<p>Hallo world</p>";
        List<Token> tokens = parse(html);
        assertEquals(3, tokens.size());
    }

    @Test
    public void shouldHandleImage() {
        Token token = parseAndReturnOneToken("<img src=x/>", 0);
        expectElementToken(token, "<img src=\"x\" />", true, false);
    }

    @Test
    public void shouldHandleEmptyImageByClaimingNotEmpty() {
        Token token = parseAndReturnOneToken("<img>", 0);
        expectElementToken(token, "<img>", false, false);
    }

    @Test
    public void doesNotLikeXmlHeader() {
        Token token = parseAndReturnOneToken("<?XML version=\"1.0\" encoding=\"UTF8\" ?>", 0);
        expectElementToken(token, "<?XML version=\"1.0\" encoding=\"UTF8\" ?>", false, true);
    }

    @Test
    public void shouldUnderstandBangTags_WhateverTheyAre_EvenIfAngledBracketHandlingIsArguablyInconsistent() {
        Token token = parseAndReturnOneToken("<!DOCTYPE>", 0);
        expectNonElementToken(token, HtmlStreamTokenizer.TT_BANGTAG, "DOCTYPE");
    }

    private void expectElementToken(Token token, String value, boolean empty, boolean invalid) {
        TokenWotIsATag elementToken = (TokenWotIsATag) token;
        assertEquals(value, elementToken.getValue());
        assertEquals(empty, elementToken.isEmpty());
        assertEquals(invalid, elementToken.isInvalid());
    }

    private void expectNonElementToken(Token token, int type, String value) {
        TokenWotIsNotATag nonElementToken = (TokenWotIsNotATag) token;
        assertEquals(type, nonElementToken.getType());
        assertEquals(value, nonElementToken.getValue());
    }

    private Token parseAndReturnOneToken(String htmlSnippet, int index) {
        return parse(htmlSnippet).get(index);
    }

    private List<Token> parse(String htmlSnippet) {
        return TokenHelper.collectTokens(htmlSnippet);
    }
}

package com.arthurdo.parser;

import com.arthurdo.parser.helper.ElementToken;
import com.arthurdo.parser.helper.OtherToken;
import com.arthurdo.parser.helper.Token;
import com.arthurdo.parser.helper.TokenHelper;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ParserTests {

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
    public void shouldHandleNonXmlNesting() {
        String html = "<b><i>Hallo world</b></i>";
        List<Token> tokens = parse(html);
        assertEquals(5, tokens.size());
        assertEquals("<b><i>Hallo world</b></i>", join(tokens));
    }

    @Test
    public void shouldHandleStartElement() {
        Token token = parseAndReturnOneToken("<p>", 0);
        expectElementToken(token, "<p>", Valid.Yes, Empty.No);
    }

    @Test
    public void shouldHandleEndElement() {
        Token token = parseAndReturnOneToken("</p>", 0);
        expectElementToken(token, "</p>", Valid.Yes, Empty.No);
    }

    @Test
    public void shouldHandleImage() {
        Token token = parseAndReturnOneToken("<img src=x/>", 0);
        expectElementToken(token, "<img src=\"x\" />", Valid.Yes, Empty.Yes);
    }

    @Test
    public void shouldHandleEmptyImageByClaimingNotEmpty() {
        Token token = parseAndReturnOneToken("<img>", 0);
        expectElementToken(token, "<img>", Valid.Yes, Empty.No);
    }

    @Test
    public void doesNotLikeXmlHeader() {
        Token token = parseAndReturnOneToken("<?XML version=\"1.0\" encoding=\"UTF8\" ?>", 0);
        expectElementToken(token, "<?XML version=\"1.0\" encoding=\"UTF8\" ?>", Valid.No, Empty.No);
    }

    @Test
    public void shouldUnderstandBangTags_WhateverTheyAre_EvenIfAngledBracketHandlingIsArguablyInconsistent() {
        Token token = parseAndReturnOneToken("<!DOCTYPE>", 0);
        expectNonElementToken(token, HtmlStreamTokenizer.TT_BANGTAG, "DOCTYPE");
    }

    @Test
    public void shouldComplainSometimes_ButDoesntSeemTo() {
        parse("<>  <elementName <another >>>>>>>>");
    }

    private void expectElementToken(Token token, String value, Valid valid, Empty empty) {
        ElementToken elementToken = (ElementToken) token;
        assertEquals(value, elementToken.getValue());
        assertEquals(empty.toBoolean(), elementToken.isEmpty());
        assertEquals(valid.toBoolean(), !elementToken.isInvalid());
    }

    private void expectNonElementToken(Token token, int type, String value) {
        OtherToken nonElementToken = (OtherToken) token;
        assertEquals(type, nonElementToken.getType());
        assertEquals(value, nonElementToken.getValue());
    }

    private Token parseAndReturnOneToken(String htmlSnippet, int index) {
        return parse(htmlSnippet).get(index);
    }

    private List<Token> parse(String htmlSnippet) {
        return TokenHelper.collectTokens(htmlSnippet);
    }

    private String join(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.getValue());
        }
        return sb.toString();
    }

    private enum Valid { Yes, No; public boolean toBoolean() {return this == Yes; } }
    private enum Empty { Yes, No; public boolean toBoolean() {return this == Yes; } }

}

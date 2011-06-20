package com.arthurdo.parser;

import com.arthurdo.parser.HtmlException;
import com.arthurdo.parser.HtmlTag;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HtmlTagTest {

    private HtmlTag _tag;

    @Before
    public void setup() {
        _tag = new HtmlTag();
    }

    @Test
    public void shouldFindH6() throws HtmlException {
        _tag.setTag("h6");
        assertEquals(HtmlTag.T_H6, _tag.getTagType());
    }

    @Test
    public void shouldNotFindH7() throws HtmlException {
        _tag.setTag("h7");
        assertEquals(HtmlTag.T_UNKNOWN, _tag.getTagType());
    }
}

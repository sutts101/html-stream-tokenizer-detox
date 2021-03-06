package com.arthurdo.parser;

public class HtmlTagParser {

    void parseTag(String buf, HtmlTag tag, boolean unescape) throws HtmlException
    {
        tag.reset();
        int cursor = advanceTillNotSpace(buf, 0);
        complainIfNoMoreChars(buf, cursor);
        cursor = advanceIfForwardSlashInWhichCaseMarkAsEndTag(buf, tag, cursor);
        complainIfNoMoreChars(buf, cursor);
        int cursor1 = cursor;
        int cursor2 = advanceTillSpaceOrForwardSlash(buf, cursor);
        tag.setTag(buf.substring(cursor1, cursor2));
        parseParams(tag, buf, cursor2, unescape);
    }

    private int advanceTillNotSpace(String buf, int idx) {
        while (idx < buf.length() && HtmlUtils.isSpace(buf.charAt(idx)))
            idx++;
        return idx;
    }

    private int advanceTillSpaceOrForwardSlash(String buf, int idx) {
        while (idx < buf.length() && !HtmlUtils.isSpace(buf.charAt(idx)) && buf.charAt(idx) != HtmlUtils.C_EMPTY)
            idx++;
        return idx;
    }

    private boolean isSpaceOrSuchlike(char c, char... udders) {
        if (HtmlUtils.isSpace(c)) {
            return true;
        }
        for (char udder : udders) {
            if (udder == c) {
                return true;
            }
        }
        return false;
    }

    private int advanceIfForwardSlashInWhichCaseMarkAsEndTag(String buf, HtmlTag tag, int idx) {
        if (buf.charAt(idx) == HtmlUtils.C_ENDTAG)
        {
            tag.setEndTag(true);
            idx++;
        }
        return idx;
    }

    private void complainIfNoMoreChars(String buf, int idx) throws HtmlException {
        if (idx == buf.length())
            throw new HtmlException("parse empty tag");
    }

    private void parseParams(HtmlTag tag, String buf, int idx, boolean unescape)
        throws HtmlException
    {
        int len = buf.length();
        int begin = 0;

        if (len-1 >= idx)
        {
            int end = len - 1;
            while (end > idx && HtmlUtils.isSpace(buf.charAt(end)))//remove trailing whitespace
                end--;
            //todo: tag.setWhitespaceAtEnd(buf.substring(end, len-1) );
            if (buf.charAt(end) == HtmlUtils.C_EMPTY)
            {
                tag.setEmpty(true);
                end--;
            }
            len = end + 1;
        }

        while (idx < len)
        {
            begin = idx;
            while (idx < len && HtmlUtils.isSpace(buf.charAt(idx)))//skip space before attribute name
                idx++;

            if (idx == len)//at end
                continue;

            String whitespaceBefore = buf.substring(begin, idx);

            begin = idx;
            if (buf.charAt(idx) == HtmlUtils.C_DOUBLEQUOTE) //how often are attribute names quoted??
            {
                idx++;
                while (idx < len && buf.charAt(idx) != HtmlUtils.C_DOUBLEQUOTE)//look for close quote
                    idx++;
                if (idx == len)
                    continue;	// bad name
                idx++;
            }
            else if (buf.charAt(idx) == HtmlUtils.C_SINGLEQUOTE) //how often are attribute names quoted??
            {
                idx++;
                while (idx < len && buf.charAt(idx) != HtmlUtils.C_SINGLEQUOTE)//look for close quote
                    idx++;
                if (idx == len)
                    continue;	// bad name
                idx++;
            }
            else
            {
                //if not quoted look for whitespace or '=' to terminate attribute name
                while (idx < len && !HtmlUtils.isSpace(buf.charAt(idx)) && buf.charAt(idx) != '=')
                    idx++;
            }

            String name = buf.substring(begin, idx);

            begin = idx;
            if (idx < len && HtmlUtils.isSpace(buf.charAt(idx)))//skip whitespace after attribute name
            {
                while (idx < len && HtmlUtils.isSpace(buf.charAt(idx)))
                    idx++;
            }

            if (idx == len || buf.charAt(idx) != '=') //attribute name only, no value specified
            {
                // name with empty value
                tag.setParam(name, name); //set the attribute name as the value (SGML tag minimalization rule)
                tag.setWhitespace(name, whitespaceBefore, "");
                continue;
            }
            idx++; //skip past the '='

            if (idx == len)
                continue;

            if (HtmlUtils.isSpace(buf.charAt(idx)))
            {
                while (idx < len && HtmlUtils.isSpace(buf.charAt(idx)))//skip past whitespace after '='
                    idx++;

                // special case: if value is surrounded by quotes
                // then it can have a space after the '='
                //if (idx == len || (buf.charAt(idx) != C_DOUBLEQUOTE && buf.charAt(idx) != C_SINGLEQUOTE))
                if (idx == len)
                {
                    // name with empty value
                    tag.setParam(name, name); //set the attribute name as the value (SGML tag minimalization rule)
                    tag.setWhitespace(name, whitespaceBefore, buf.substring(begin, idx));
                    continue;
                }
            }

            char quote = buf.charAt(idx);
            int includeQuote = (quote == HtmlUtils.C_DOUBLEQUOTE || quote == HtmlUtils.C_SINGLEQUOTE) ? 1 : 0;
            String whitespaceAfter = buf.substring(begin, idx + includeQuote);

            begin = idx;
            int end = begin;
            if (quote == HtmlUtils.C_DOUBLEQUOTE)
            {
                idx++;
                begin = idx;
                while (idx < len && buf.charAt(idx) != HtmlUtils.C_DOUBLEQUOTE)
                    idx++;
                if (idx == len)
                    continue;	// bad value
                end = idx;
                idx++;
            }
            else if (quote == HtmlUtils.C_SINGLEQUOTE)
            {
                idx++;
                begin = idx;
                while (idx < len && buf.charAt(idx) != HtmlUtils.C_SINGLEQUOTE)
                    idx++;
                if (idx == len)
                    continue;	// bad value
                end = idx;
                idx++;
            }
            else
            {//not quoted, whitespace terminates attribute value
                while (idx < len && !HtmlUtils.isSpace(buf.charAt(idx)))
                    idx++;
                end = idx;
            }

            String value = buf.substring(begin, end);

            if (unescape)
                value = HtmlEscaping.unescape(value);

            tag.setParam(name, value);
            tag.setWhitespace(name, whitespaceBefore, whitespaceAfter);
        }
    }



}

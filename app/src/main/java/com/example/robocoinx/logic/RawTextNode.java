package com.example.robocoinx.logic;

import org.jsoup.nodes.TextNode;

public class RawTextNode extends TextNode {

    public RawTextNode(String text) {
        super(text);
    }

    @Override
    protected void outerHtml(Appendable accum) {
        super.outerHtml(accum);
    }
}
package com.bureng.robocoinx.utils

import android.text.Editable
import android.text.Html.TagHandler
import org.xml.sax.XMLReader

class HtmlTagHandler : TagHandler {
    var first = true
    var parent: String? = null
    var index = 1
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        if (tag == "ul") {
            parent = "ul"
        } else if (tag == "ol") {
            parent = "ol"
        }
        if (tag == "li") {
            if (parent == "ul") {
                first = if (first) {
                    output.append("\n\t•")
                    false
                } else {
                    true
                }
            } else {
                if (first) {
                    output.append("\n\t$index. ")
                    first = false
                    index++
                } else {
                    first = true
                }
            }
        }
    }
}
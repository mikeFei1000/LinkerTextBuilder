package com.longbridge.common.uiLib.linker

import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import java.util.regex.Matcher
import java.util.regex.Pattern

object Linker {

    fun parseHtml(text: String, color: Int, bold: Boolean, shouldShowUnderLine: Boolean, linkClickListener: OnLinkClickListener?): Spanned {
        val html = Html.fromHtml(text.replace("\n","<br />"))
        val spans = html.getSpans(0, text.length, URLSpan::class.java)
        val builder = SpannableStringBuilder(html)
        builder.clearSpans()
        for (span in spans) {
            //点击事件
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    linkClickListener?.onClick(span.url)
                }

                override fun updateDrawState(ds: TextPaint) {
                    if (color != 0) {
                        ds.color = color
                    }
                    ds.isFakeBoldText = bold
                    ds.isUnderlineText = shouldShowUnderLine
                }
            }, html.getSpanStart(span), html.getSpanEnd(span), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return builder
    }

    class Builder {

        private var linkMovementMethod: MovementMethod? = null
        private lateinit var textView: TextView
        private lateinit var content: String
        private var links: List<String> = ArrayList()
        private var color: Int = Color.BLACK
        private var shouldShowUnderLine: Boolean = false
        private var linkClickListener: OnLinkClickListener? = null
        private var colorLinks: List<Pair<String, Int>> = ArrayList()
        private var bold = false

        fun textView(textView: TextView): Builder {
            this.textView = textView
            return this
        }

        fun content(content: String): Builder {
            this.content = content
            return this
        }

        fun links(link: String): Builder {
            return links(arrayOf(link).asList())
        }

        fun links(vararg links: String): Builder {
            return links(links.asList())
        }

        fun links(links: List<String>): Builder {
            this.links = links
            return this
        }

        fun colorLinks(links: List<Pair<String, Int>>): Builder {
            this.colorLinks = links
            return this
        }


        fun linkColor(color: Int): Builder {
            this.color = color
            return this
        }

        fun shouldShowUnderLine(shouldShowUnderLine: Boolean): Builder {
            this.shouldShowUnderLine = shouldShowUnderLine
            return this
        }

        fun addOnLinkClickListener(listener: OnLinkClickListener): Builder {
            this.linkClickListener = listener
            return this
        }

        fun setLinkMovementMethod(method: MovementMethod): Builder {
            this.linkMovementMethod = method
            return this
        }

        fun bold(bold: Boolean): Builder {
            this.bold = bold
            return this
        }

        fun apply() {
            if (links.isNullOrEmpty()) {
                applyHrefLink(textView, content, color, bold, shouldShowUnderLine, linkClickListener)
            } else {
                applyLink(textView, content, links, color, shouldShowUnderLine, linkClickListener, linkMovementMethod, colorLinks, bold)
            }
        }
    }

    fun applyHrefLink(textView: TextView?, text: String, color: Int, bold: Boolean, shouldShowUnderLine: Boolean, linkClickListener: OnLinkClickListener?) {
        textView?.text = parseHtml(text, color, bold, shouldShowUnderLine, linkClickListener)
        textView?.movementMethod = LinkMovementMethod.getInstance()
    }

    fun applyLink(
        textView: TextView?,
        content: String,
        links: List<String>?,
        color: Int,
        shouldShowUnderLine: Boolean,
        linkClickListener: OnLinkClickListener?,
        linkMovementMethod: MovementMethod?,
        colorLinks: List<Pair<String, Int>>?,
        bold: Boolean
    ) {
        if (textView == null) {
            return
        }

        if ((links == null || links.isEmpty()) && (colorLinks == null || colorLinks.isEmpty())) {
            textView.text = content
            return
        }

        if (colorLinks != null && colorLinks.isNotEmpty()) {
            applyLinkInternal(textView, content, colorLinks, shouldShowUnderLine, linkClickListener, linkMovementMethod, bold)
            return
        }

        if (links != null && links.isNotEmpty()) {
            var colorAndLinks = arrayListOf<Pair<String, Int>>()
            for (link in links) {
                colorAndLinks.add(Pair(link, color))
            }
            applyLinkInternal(textView, content, colorAndLinks, shouldShowUnderLine, linkClickListener, linkMovementMethod, bold)
        }

    }

    private fun applyLinkInternal(
        textView: TextView, content: String, links: List<Pair<String, Int>>,
        shouldShowUnderLine: Boolean,
        linkClickListener: OnLinkClickListener?,
        linkMovementMethod: MovementMethod?, bold: Boolean
    ) {

        val spannableString = SpannableString(content)

        var pattern: Pattern?
        var matcher: Matcher?
        var clickableSpan: ClickableSpan?


        for (value in links) {
            if (TextUtils.isEmpty(value.first)) {
                continue
            }

            pattern = Pattern.compile(value.first)
            matcher = pattern.matcher(content)
            while (matcher.find()) {
                clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        linkClickListener?.onClick(value.first)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        if (value.second != 0) {
                            ds.color = value.second
                        }
                        ds.isFakeBoldText = bold
                        ds.isUnderlineText = shouldShowUnderLine
                    }
                }

                spannableString.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        textView.text = spannableString

        if (linkMovementMethod != null) {
            textView.movementMethod = linkMovementMethod
        } else {
            textView.movementMethod = TextViewLinkMovementMethod().getInstance()
        }
    }
}

package com.sharpdroid.registroelettronico.Fragments


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.text.Html
import android.text.util.Linkify
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.Utils.Metodi

class FragmentCredits : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val scroll = NestedScrollView(activity)
        val textView = TextView(activity)
        textView.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        textView.setPadding(Metodi.dp(16), Metodi.dp(16), Metodi.dp(16), Metodi.dp(16))
        textView.text = Html.fromHtml(credits)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        Linkify.addLinks(textView, Linkify.ALL)
        textView.setTextColor(Color.parseColor("#AA000000"))
        textView.setLinkTextColor(Color.parseColor("#44000000"))
        scroll.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        scroll.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return scroll
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Riconoscimenti"
    }

    companion object {
        val credits = "<h2>Sviluppo Android</h2>" +
                "Bortolan Marco <br/>bortolanmarco@gmail.com<br/><br/>\n" +
                "Stefani Luca <br/>luca.stefani.ge1@gmail.com<br/><br/>\n" +
                "Luconi Simone <br/>info@simoneluconi.com<br/><br/>" +
                "<h2>Grafica</h2>" +
                "Dalla Giustina Davide<br/>davide@dellagiustina.com<br/><br/>" +
                "Bortolan Marco<br/>bortolanmarco@gmail.com<br/><br/>" +
                "<h2>Sviluppo server</h2>" +
                "Monteleone Daniele<br/>daniele@monteleone.ml"
    }

}

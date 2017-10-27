package com.sharpdroid.registroelettronico.Activities

import android.Manifest
import android.os.Build
import android.os.Bundle

import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.sharpdroid.registroelettronico.Fragments.FragmentLogin
import com.sharpdroid.registroelettronico.R

class Intro : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(SimpleSlide.Builder()
                .title(getString(R.string.benvenuto))
                .description(R.string.intro_app_desc)
                .image(R.mipmap.ic_launcher)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
        addSlide(SimpleSlide.Builder()
                .title(R.string.dettagli_medie_titolo)
                .description(R.string.dettaglio_media_desc)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
        addSlide(SimpleSlide.Builder()
                .title(R.string.media_ipotetica_titolo)
                .description(R.string.media_ipotetica_desc)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
        addSlide(SimpleSlide.Builder()
                .title(R.string.dettaglio_voti_titolo)
                .description(R.string.dettaglio_voti_desc)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
        addSlide(SimpleSlide.Builder()
                .title(R.string.agenda_titolo)
                .description(R.string.agenda_desc)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(SimpleSlide.Builder()
                    .title(R.string.ultima_cosa_titolo)
                    .description(R.string.ultima_cosa_desc)
                    .background(R.color.intro_blue)
                    .backgroundDark(R.color.intro_blue_dark)
                    .permissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
                    .build())
        }
        addSlide(FragmentSlide.Builder()
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .fragment(FragmentLogin.newInstance())
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.sei_pronto_titolo)
                .description(R.string.sei_pronto_desc)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build())
    }
}

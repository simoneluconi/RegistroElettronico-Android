package com.sharpdroid.registroelettronico.activities

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.LoginAdapter
import com.sharpdroid.registroelettronico.api.v2.APIClient
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.Option
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser
import com.sharpdroid.registroelettronico.utils.Metodi.loginFeedback
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val p = AppCompatResources.getDrawable(this, R.drawable.ic_person)
        val l = AppCompatResources.getDrawable(this, R.drawable.ic_password)

        p?.setColorFilter(ContextCompat.getColor(this, android.R.color.secondary_text_dark), PorterDuff.Mode.SRC_IN)
        l?.setColorFilter(ContextCompat.getColor(this, android.R.color.secondary_text_dark), PorterDuff.Mode.SRC_IN)

        mail.setCompoundDrawablesWithIntrinsicBounds(p, null, null, null)
        password.setCompoundDrawablesWithIntrinsicBounds(l, null, null, null)

        val s = intent.getStringExtra("user")
        if (s != null)
            mail.setText(s)

        login_btn.setOnClickListener { login() }
        password.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                login()
                true
            } else {
                false
            }
        }
    }

    private fun login() {
        val mEmail = mail.text.toString()
        val mPassword = password.text.toString()

        mail.isEnabled = false
        password.isEnabled = false
        login_btn.isEnabled = false
        login_btn.setText(R.string.caricamento)

        APIClient.with(null).postLogin(LoginRequest(mPassword, mEmail, ""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ login ->
                    if (login.choices != null) {
                        val it = SugarRecord.findAll(Profile::class.java)
                        while (it.hasNext()) {
                            val (_, _, _, _, id) = it.next()
                            val toRemove = login.choices.filter { it.ident.substring(1, 8) == id.toString() }
                            //remove already logged in profiles
                            login.choices.removeAll(toRemove)
                        }

                        if (login.choices.isNotEmpty()) {
                            val checkedIdents = ArrayList<String>()
                            val builder = MaterialDialog.Builder(this).title("Account multiplo").content("Seleziona gli account che vuoi importare").positiveText("OK").neutralText("Annulla")
                                    .alwaysCallMultiChoiceCallback()
                                    .dividerColor(Color.TRANSPARENT)
                                    .adapter(LoginAdapter(login.choices, this@LoginActivity) { idents ->
                                        checkedIdents.clear()
                                        checkedIdents.addAll(idents)
                                        println(checkedIdents)
                                        Unit
                                    }, LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false))
                                    .onPositive { _, _ ->
                                        for (ident in checkedIdents) {
                                            loginWithIdent(mEmail, mPassword, ident)
                                        }
                                        if (!BuildConfig.DEBUG)
                                            Answers.getInstance().logLogin(LoginEvent().putMethod("multiple"))
                                    }
                                    .canceledOnTouchOutside(false)
                                    .onNeutral { _, _ ->
                                        login_btn.setText(R.string.login)
                                        mail.isEnabled = true
                                        password.isEnabled = true
                                        login_btn.isEnabled = true
                                    }
                            builder.show()
                        } else {
                            Toast.makeText(this, "Tutti gli account collegati alla mail sono già in uso", Toast.LENGTH_SHORT).show()
                            super.onBackPressed()
                        }
                    } else {
                        SugarRecord.save(Option(login.ident!!.substring(1, 8).toLong(), true, true, true, true, true))
                        SugarRecord.save(Profile(mEmail, login.firstName + " " + login.lastName, mPassword, "", login.ident.substring(1, 8).toLong(), login.token!!, login.expire!!.time, login.ident, false))
                        Account.with(this).user = login.ident.substring(1, 8).toLong()
                        fetchDataOfUser(this)

                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply()
                        login_btn.setText(R.string.login_riuscito)
                        if (!BuildConfig.DEBUG)
                            Answers.getInstance().logLogin(LoginEvent().putMethod("single"))
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }) { error ->
                    loginFeedback(error, this)

                    login_btn.setText(R.string.login)
                    mail.isEnabled = true
                    password.isEnabled = true
                    login_btn.isEnabled = true
                }
    }

    private fun loginWithIdent(email: String, password: String, ident: String) {
        val c = this
        APIClient.with(null).postLogin(LoginRequest(password, email, ident))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (ident1, firstName, lastName, token, expire) ->
                    SugarRecord.save(Option(ident1!!.substring(1, 8).toLong(), true, true, true, true, true))
                    SugarRecord.save(Profile(email, firstName + " " + lastName, password, "", ident1.substring(1, 8).toLong(), token!!, expire!!.time, ident1, true))
                    Account.with(c).user = ident1.substring(1, 8).toLong()
                    fetchDataOfUser(c)

                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("first_run", false).apply()
                    login_btn.setText(R.string.login_riuscito)
                    setResult(Activity.RESULT_OK)
                    finish()
                }) { error ->
                    loginFeedback(error, c)

                    login_btn.setText(R.string.login)
                    mail.isEnabled = true
                    this.password.isEnabled = true
                    login_btn.isEnabled = true
                }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

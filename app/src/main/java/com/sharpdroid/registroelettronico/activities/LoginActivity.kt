package com.sharpdroid.registroelettronico.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.LoginAdapter
import com.sharpdroid.registroelettronico.api.spaggiari.v2.Spaggiari
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.LoginResponse
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser
import com.sharpdroid.registroelettronico.utils.Metodi.loginFeedback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

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

    @SuppressLint("CheckResult")
    private fun login() {
        val mEmail = mail.text.toString()
        val mPassword = password.text.toString()

        mail.isEnabled = false
        password.isEnabled = false
        login_btn.isEnabled = false
        login_btn.setText(R.string.caricamento)

        Spaggiari(null).api().postLogin(LoginRequest(mPassword, mEmail, ""))
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ login ->
            if (login.choices != null) {
                val it = DatabaseHelper.database.profilesDao().profilesSync.iterator()
                while (it.hasNext()) {
                    val profile = it.next()
                    val toRemove = login.choices.filter { it.ident.substring(1, 8) == profile.id.toString() }
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
                                loginWithIdent(mEmail, mPassword, checkedIdents.toTypedArray()).subscribe({ t ->
                                    val profile = Profile(mEmail, t.firstName + " " + t.lastName, mPassword, "", t.ident!!.substring(1, 8).toLong(), t.token!!, t.expire!!, t.ident, true)
                                    DatabaseHelper.database.profilesDao().insert(profile)
                                    fetchDataOfUser(profile)

                                }, {
                                    loginFeedback(it, this)

                                    login_btn.setText(R.string.login)
                                    mail.isEnabled = true
                                    this.password.isEnabled = true
                                    login_btn.isEnabled = true
                                }, {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                })
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
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            } else {
                DatabaseHelper.database.profilesDao().insert(Profile(mEmail, login.firstName + " " + login.lastName, mPassword, "", login.ident!!.substring(1, 8).toLong(), login.token!!, login.expire!!, login.ident, false))
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

    @SuppressLint("CheckResult")
    private fun loginWithIdent(email: String, password: String, idents: Array<String>): Observable<LoginResponse> {
        // This asterisk is called spread operator. It converts an Array to vararg
        return Observable.fromArray(*idents.map { id -> Spaggiari(null).api().postLogin(LoginRequest(password, email, id)) }.toTypedArray())
                .flatMap { t -> t.subscribeOn(Schedulers.computation()) }
                .observeOn(AndroidSchedulers.mainThread())

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

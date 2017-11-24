package com.sharpdroid.registroelettronico.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.LoginAdapter
import com.sharpdroid.registroelettronico.api.spiaggiari.v2.APIClient
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser
import com.sharpdroid.registroelettronico.utils.Metodi.loginFeedback
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.HttpException
import java.util.*

class FragmentLogin : SlideFragment() {
    private var loggedIn = false
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mContext = context
        return inflater.inflate(R.layout.fragment_login_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mail.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext!!, R.drawable.ic_person), null, null, null)
        password.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext!!, R.drawable.ic_password), null, null, null)

        mail.isEnabled = !loggedIn
        password.isEnabled = !loggedIn
        login_btn.isEnabled = !loggedIn

        login_btn.setOnClickListener { login() }
        password.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                login()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun canGoForward() = loggedIn

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
                    val checkedIdents = ArrayList<String>()
                    if (login.choices != null) {
                        val builder = MaterialDialog.Builder(context).title("Account multiplo").content("Seleziona gli account che vuoi importare").positiveText("OK").neutralText("Annulla")
                                .alwaysCallMultiChoiceCallback()
                                .dividerColor(Color.TRANSPARENT)
                                .adapter(LoginAdapter(login.choices, context) { checked ->
                                    checkedIdents.clear()
                                    checkedIdents.addAll(checked)
                                    Unit
                                }, LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
                                .onPositive { _, _ ->
                                    for (ident in checkedIdents) {
                                        loginWithIdent(mEmail, mPassword, ident)
                                    }
                                    postLogin()
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
                        DatabaseHelper.database.profilesDao().insert(Profile(mEmail, login.firstName + " " + login.lastName, mPassword, "", java.lang.Long.valueOf(login.ident!!.substring(1, 8)), login.token!!, login.expire!!, login.ident, false))

                        Account.with(activity).user = java.lang.Long.valueOf(login.ident.substring(1, 8))
                        fetchDataOfUser(activity)
                        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                .putBoolean("first_run", false)
                                .apply()

                        login_btn.setText(R.string.login_riuscito)
                        postLogin()
                    }
                }) { error ->
                    if (error is HttpException) {
                        Log.e("FragmentLogin", error.response().errorBody()?.string())
                    }

                    loginFeedback(error, context)

                    login_btn.setText(R.string.login)
                    mail.isEnabled = true
                    password.isEnabled = true
                    login_btn.isEnabled = true
                }
    }

    private fun postLogin() {
        loggedIn = true
        updateNavigation()
        nextSlide()
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logLogin(LoginEvent().putMethod("multiple"))
    }

    private fun loginWithIdent(email: String, password: String, ident: String) {
        val c = context
        APIClient.with(null).postLogin(LoginRequest(password, email, ident))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ login_nested ->
                    DatabaseHelper.database.profilesDao().insert(Profile(email, login_nested.firstName + " " + login_nested.lastName, password, "", java.lang.Long.valueOf(login_nested.ident!!.substring(1, 8)), login_nested.token!!, login_nested.expire!!, login_nested.ident, true))
                    Account.with(c).user = java.lang.Long.valueOf(login_nested.ident.substring(1, 8))
                    fetchDataOfUser(c)

                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("first_run", false).apply()
                    login_btn.setText(R.string.login_riuscito)
                }) { error ->
                    loginFeedback(error, c)

                    login_btn.setText(R.string.login)
                    mail.isEnabled = true
                    this.password.isEnabled = true
                    login_btn.isEnabled = true
                }
    }
}
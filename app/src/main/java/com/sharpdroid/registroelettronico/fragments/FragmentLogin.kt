package com.sharpdroid.registroelettronico.fragments

import android.annotation.SuppressLint
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
import com.sharpdroid.registroelettronico.api.spaggiari.v2.Spaggiari
import com.sharpdroid.registroelettronico.database.entities.LoginRequest
import com.sharpdroid.registroelettronico.database.entities.LoginResponse
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser
import com.sharpdroid.registroelettronico.utils.Metodi.loginFeedback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
                            loginWithIdent(mEmail, mPassword, checkedIdents.toTypedArray())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ t ->
                                        val profile = Profile(mEmail, t.firstName + " " + t.lastName, mPassword, "", t.ident!!.substring(1, 8).toLong(), t.token!!, t.expire!!, t.ident, true)
                                        DatabaseHelper.database.profilesDao().insert(profile)
                                        fetchDataOfUser(profile)
                                    }, {
                                        loginFeedback(it, context)

                                        login_btn.setText(R.string.login)
                                        mail.isEnabled = true
                                        this.password.isEnabled = true
                                        login_btn.isEnabled = true
                                    }, {
                                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("first_run", false).apply()
                                        login_btn.setText(R.string.login_riuscito)

                                        postLogin()
                                    })

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

    private fun loginWithIdent(email: String, password: String, idents: Array<String>): io.reactivex.Observable<LoginResponse> {
        // This asterisk is called spread operator. It converts an Array to vararg
        return io.reactivex.Observable.fromArray(*idents.map { id -> Spaggiari(null).api().postLogin(LoginRequest(password, email, id)) }.toTypedArray())
                .flatMap { t -> t.subscribeOn(Schedulers.computation()) }
                .observeOn(AndroidSchedulers.mainThread())
    }
}
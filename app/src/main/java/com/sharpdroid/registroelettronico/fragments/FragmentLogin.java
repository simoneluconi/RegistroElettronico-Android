package com.sharpdroid.registroelettronico.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.BuildConfig;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.adapters.LoginAdapter;
import com.sharpdroid.registroelettronico.api.v2.APIClient;
import com.sharpdroid.registroelettronico.database.entities.LoginRequest;
import com.sharpdroid.registroelettronico.database.entities.Option;
import com.sharpdroid.registroelettronico.database.entities.Profile;
import com.sharpdroid.registroelettronico.utils.Account;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import retrofit2.HttpException;

import static com.sharpdroid.registroelettronico.utils.Metodi.fetchDataOfUser;
import static com.sharpdroid.registroelettronico.utils.Metodi.loginFeedback;

@SuppressWarnings("ConstantConditions")
public class FragmentLogin extends SlideFragment {

    @BindView(R.id.mail)
    TextInputEditText mEditTextMail;
    @BindView(R.id.password)
    TextInputEditText mEditTextPassword;
    @BindView(R.id.login_btn)
    Button mButtonLogin;

    private boolean loggedIn = false;
    private Context mContext;

    public FragmentLogin() {
        // Required empty public constructor
    }

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mContext = getContext();
        return inflater.inflate(R.layout.fragment_login_light, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mEditTextMail.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_person), null, null, null);
        mEditTextPassword.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(mContext, R.drawable.ic_password), null, null, null);

        mEditTextMail.setEnabled(!loggedIn);
        mEditTextPassword.setEnabled(!loggedIn);
        mButtonLogin.setEnabled(!loggedIn);

        mButtonLogin.setOnClickListener(v -> login());
        mEditTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    private void login() {
        String mEmail = mEditTextMail.getText().toString();
        String mPassword = mEditTextPassword.getText().toString();

        mEditTextMail.setEnabled(false);
        mEditTextPassword.setEnabled(false);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setText(R.string.caricamento);

        APIClient.Companion.with(null).postLogin(new LoginRequest(mPassword, mEmail, ""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    final List<String> checkedIdents = new ArrayList<>();
                    if (login.getChoices() != null) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext()).title("Account multiplo").content("Seleziona gli account che vuoi importare").positiveText("OK").neutralText("Annulla")
                                .alwaysCallMultiChoiceCallback()
                                .dividerColor(Color.TRANSPARENT)
                                .adapter(new LoginAdapter(login.getChoices(), getContext(), (checked) -> {
                                    checkedIdents.clear();
                                    checkedIdents.addAll(checked);
                                    System.out.println(checked.toString());
                                    return Unit.INSTANCE;
                                }), new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false))
                                .onPositive((materialDialog, dialogAction) -> {
                                    for (String ident : checkedIdents) {
                                        loginWithIdent(mEmail, mPassword, ident);
                                    }
                                    postLogin();
                                })
                                .canceledOnTouchOutside(false)
                                .onNeutral((dialog, which) -> {
                                    mButtonLogin.setText(R.string.login);
                                    mEditTextMail.setEnabled(true);
                                    mEditTextPassword.setEnabled(true);
                                    mButtonLogin.setEnabled(true);
                                });
                        builder.show();
                    } else {
                        SugarRecord.save(new Option(Long.valueOf(login.getIdent().substring(1, 8)), true, true, true, true, true));
                        SugarRecord.save(new Profile(login.getIdent(), login.getFirstName() + " " + login.getLastName(), mPassword, "", Long.valueOf(login.getIdent().substring(1, 8)), login.getToken(), login.getExpire().getTime()));

                        Account.Companion.with(getActivity()).setUser(Long.valueOf(login.getIdent().substring(1, 8)));
                        fetchDataOfUser(getActivity());
                        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                .putBoolean("first_run", false)
                                .apply();

                        mButtonLogin.setText(R.string.login_riuscito);
                        postLogin();
                    }
                }, error -> {
                    if (error instanceof HttpException) {
                        Log.e("FragmentLogin", ((HttpException) error).response().errorBody().string());
                    }

                    loginFeedback(error, getContext());

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

    private void postLogin() {
        loggedIn = true;
        updateNavigation();
        nextSlide();
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logLogin(new LoginEvent().putMethod("multiple"));
    }

    private void loginWithIdent(String email, String password, String ident) {
        Context c = getContext();
        APIClient.Companion.with(null).postLogin(new LoginRequest(password, email, ident))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login_nested -> {
                    SugarRecord.save(new Option(Long.valueOf(login_nested.getIdent().substring(1, 8)), true, true, true, true, true));
                    SugarRecord.save(new Profile(login_nested.getIdent(), login_nested.getFirstName() + " " + login_nested.getLastName(), password, "", Long.valueOf(login_nested.getIdent().substring(1, 8)), login_nested.getToken(), login_nested.getExpire().getTime()));
                    Account.Companion.with(c).setUser(Long.valueOf(login_nested.getIdent().substring(1, 8)));
                    fetchDataOfUser(c);

                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("first_run", false).apply();
                    mButtonLogin.setText(R.string.login_riuscito);
                }, error -> {
                    loginFeedback(error, c);

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }
}
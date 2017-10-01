package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest;
import com.sharpdroid.registroelettronico.Databases.Entities.Profile;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Account;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.HttpException;

import static com.sharpdroid.registroelettronico.Utils.Metodi.loginFeedback;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mContext = getContext();
        return inflater.inflate(R.layout.fragment_login_light, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

        APIClient.Companion.with(mContext).postLogin(new LoginRequest(mPassword, mEmail))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    SugarRecord.save(new Profile(mEmail, login.getFirstName() + " " + login.getLastName(), mPassword, "", Long.valueOf(login.getIdent().substring(1, 8)), login.getToken(), login.getExpire().getTime()));

                    Account.Companion.with(getActivity()).setUser(Long.valueOf(login.getIdent().substring(1, 8)));
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                            .putBoolean("first_run", false)
                            .apply();

                    mButtonLogin.setText(R.string.login_riuscito);
                    loggedIn = true;
                    updateNavigation();
                    nextSlide();
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

}
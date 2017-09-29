package com.sharpdroid.registroelettronico.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest;
import com.sharpdroid.registroelettronico.Databases.Entities.Profile;
import com.sharpdroid.registroelettronico.Info;
import com.sharpdroid.registroelettronico.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

        String oldProfile = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Info.ACCOUNT, "");
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(Info.ACCOUNT, mEmail).apply();
        //RegistroDB db = RegistroDB.getInstance(getContext());
        new Profile(mEmail, "", "").save();
        //INSERT IN DB BEFORE REQUEST TO AVOID CONSTRAINT ERRORS
        //db.addProfile(new ProfileDrawerItem().withEmail(mEmail));

        APIClient.Companion.with(mContext).postLogin(new LoginRequest(mPassword, mEmail))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    //db.updateProfile(new ProfileDrawerItem().withName(WordUtils.capitalizeFully(login.getName())).withEmail(mEmail));
                    Profile p = Profile.find(Profile.class, "username =?", mEmail).get(0);
                    p.setName(login.getFirstName() + " " + login.getLastName());
                    p.save();
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                            .putString(Info.ACCOUNT, mEmail)
                            .putString(Info.Spaggiari.IDENT, login.getIdent().substring(1, 7))
                            .putLong(Info.Spaggiari.EXPIRE, login.getExpire().getTime())
                            .putString(Info.Spaggiari.TOKEN, login.getToken())
                            .putBoolean("first_run", false)
                            .apply();

                    mButtonLogin.setText(R.string.login_riuscito);
                    //Toast.makeText(mContext, R.string.login_msg, Toast.LENGTH_SHORT).show();
                    loggedIn = true;
                    updateNavigation();
                    nextSlide();
                }, error -> {
                    loginFeedback(error, getContext());

                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(Info.ACCOUNT, oldProfile).apply();
                    //db.removeProfile(mEmail);

                    Profile p = Profile.find(Profile.class, "username =?", mEmail).get(0);
                    p.save();

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

}
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
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.DeviceUuidFactory;

import org.apache.commons.lang3.text.WordUtils;

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

        mButtonLogin.setOnClickListener(v -> Login());
        mEditTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                Login();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    private void Login() {
        String mEmail = mEditTextMail.getText().toString();
        String mPassword = mEditTextPassword.getText().toString();

        mEditTextMail.setEnabled(false);
        mEditTextPassword.setEnabled(false);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setText(R.string.caricamento);

        String oldProfile = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("currentProfile", "");
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("currentProfile", mEmail).apply();
        RegistroDB db = RegistroDB.Companion.getInstance(getContext());
        //INSERT IN DB BEFORE REQUEST TO AVOID CONSTRAINT ERRORS
        db.addProfile(new ProfileDrawerItem().withEmail(mEmail));

        new SpiaggiariApiClient(mContext).postLogin(mEmail, mPassword, new DeviceUuidFactory(mContext).getDeviceUuid().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    db.updateProfile(new ProfileDrawerItem().withName(WordUtils.capitalizeFully(login.getName())).withEmail(mEmail));

                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("currentProfile", mEmail).putBoolean("first_run", false).apply();

                    mButtonLogin.setText(R.string.login_riuscito);
                    Toast.makeText(mContext, R.string.login_msg, Toast.LENGTH_SHORT).show();
                    loggedIn = true;
                    updateNavigation();
                    nextSlide();
                }, error -> {
                    loginFeedback(error, getContext());

                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("currentProfile", oldProfile).apply();
                    db.removeProfile(mEmail);

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

}
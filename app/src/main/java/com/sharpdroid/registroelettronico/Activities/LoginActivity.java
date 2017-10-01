package com.sharpdroid.registroelettronico.Activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest;
import com.sharpdroid.registroelettronico.Databases.Entities.Profile;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Account;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.loginFeedback;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.mail)
    TextInputEditText mEditTextMail;
    @BindView(R.id.password)
    TextInputEditText mEditTextPassword;
    @BindView(R.id.login_btn)
    Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        ButterKnife.bind(this);

        Drawable p = AppCompatResources.getDrawable(this, R.drawable.ic_person);
        Drawable l = AppCompatResources.getDrawable(this, R.drawable.ic_password);

        p.setColorFilter(ContextCompat.getColor(this, android.R.color.secondary_text_dark), PorterDuff.Mode.SRC_IN);
        l.setColorFilter(ContextCompat.getColor(this, android.R.color.secondary_text_dark), PorterDuff.Mode.SRC_IN);

        mEditTextMail.setCompoundDrawablesWithIntrinsicBounds(p, null, null, null);
        mEditTextPassword.setCompoundDrawablesWithIntrinsicBounds(l, null, null, null);

        String s = getIntent().getStringExtra("user");
        if (s != null)
            mEditTextMail.setText(s);

        mButtonLogin.setOnClickListener(v -> Login());
        mEditTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                Login();
                return true;
            }
            return false;
        });
    }

    private void Login() {
        String mEmail = mEditTextMail.getText().toString();
        String mPassword = mEditTextPassword.getText().toString();

        mEditTextMail.setEnabled(false);
        mEditTextPassword.setEnabled(false);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setText(R.string.caricamento);

        APIClient.Companion.with(this).postLogin(new LoginRequest(mPassword, mEmail))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {

                    SugarRecord.save(new Profile(mEmail, login.getFirstName() + " " + login.getLastName(), mPassword, "", Long.valueOf(login.getIdent().substring(1, 8)), login.getToken(), login.getExpire().getTime()));
                    Account.Companion.with(this).setUser(Long.valueOf(login.getIdent().substring(1, 8)));

                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
                    mButtonLogin.setText(R.string.login_riuscito);
                    Toast.makeText(this, R.string.login_msg, Toast.LENGTH_SHORT).show();
                    finish();
                }, error -> {
                    loginFeedback(error, this);

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

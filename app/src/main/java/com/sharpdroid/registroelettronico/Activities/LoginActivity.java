package com.sharpdroid.registroelettronico.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.API.V2.APIClient;
import com.sharpdroid.registroelettronico.Adapters.LoginAdapter;
import com.sharpdroid.registroelettronico.Databases.Entities.Choice;
import com.sharpdroid.registroelettronico.Databases.Entities.LoginRequest;
import com.sharpdroid.registroelettronico.Databases.Entities.Option;
import com.sharpdroid.registroelettronico.Databases.Entities.Profile;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.Account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;

import static com.sharpdroid.registroelettronico.Utils.Metodi.fetchDataOfUser;
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

        APIClient.Companion.with(this, null).postLogin(new LoginRequest(mPassword, mEmail, ""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    if (login.getChoices() != null) {
                        for (Iterator<Profile> it = SugarRecord.findAll(Profile.class); it.hasNext(); ) {
                            Profile p = it.next();

                            //remove already logged in profiles
                            for (Choice c : login.getChoices()) {
                                if (c.getIdent().substring(1, 8).equals(String.valueOf(p.getId()))) {
                                    login.getChoices().remove(c);
                                    break;
                                }
                            }
                        }

                        if (login.getChoices().size() > 0) {
                            final List<String> checkedIdents = new ArrayList<>();
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(this).title("Account multiplo").content("Seleziona gli account che vuoi importare").positiveText("OK").neutralText("Annulla")
                                    .alwaysCallMultiChoiceCallback()
                                    .dividerColor(Color.TRANSPARENT)
                                    .adapter(new LoginAdapter(mPassword, mEmail, login.getChoices(), LoginActivity.this, (idents) -> {
                                        checkedIdents.clear();
                                        checkedIdents.addAll(idents);
                                        System.out.println(checkedIdents);
                                        return Unit.INSTANCE;
                                    }), new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false))
                                    .onPositive((materialDialog, dialogAction) -> {
                                        for (String ident : checkedIdents) {
                                            loginWithIdent(mEmail, mPassword, ident);
                                        }
                                    })
                                    .onNeutral((dialog, which) -> {
                                        mButtonLogin.setText(R.string.login);
                                        mEditTextMail.setEnabled(true);
                                        mEditTextPassword.setEnabled(true);
                                        mButtonLogin.setEnabled(true);
                                    });
                            builder.show();
                        } else {
                            Toast.makeText(this, "Tutti gli account collegati alla mail sono già in uso", Toast.LENGTH_SHORT).show();
                            super.onBackPressed();
                        }
                    } else {
                        SugarRecord.save(new Option(Long.valueOf(login.getIdent().substring(1, 8)), true, true, true, true, true));
                        SugarRecord.save(new Profile(mEmail, login.getFirstName() + " " + login.getLastName(), mPassword, "", Long.valueOf(login.getIdent().substring(1, 8)), login.getToken(), login.getExpire().getTime()));
                        Account.Companion.with(this).setUser(Long.valueOf(login.getIdent().substring(1, 8)));
                        fetchDataOfUser(this);

                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
                        mButtonLogin.setText(R.string.login_riuscito);
                        finish();
                    }
                }, error -> {
                    loginFeedback(error, this);

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

    private void loginWithIdent(String email, String password, String ident) {
        Activity c = this;
        APIClient.Companion.with(c, null).postLogin(new LoginRequest(password, email, ident))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login_nested -> {
                    SugarRecord.save(new Option(Long.valueOf(login_nested.getIdent().substring(1, 8)), true, true, true, true, true));
                    SugarRecord.save(new Profile(email, login_nested.getFirstName() + " " + login_nested.getLastName(), password, "", Long.valueOf(login_nested.getIdent().substring(1, 8)), login_nested.getToken(), login_nested.getExpire().getTime()));
                    Account.Companion.with(c).setUser(Long.valueOf(login_nested.getIdent().substring(1, 8)));
                    fetchDataOfUser(c);

                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("first_run", false).apply();
                    mButtonLogin.setText(R.string.login_riuscito);
                    c.onBackPressed();
                }, error -> {
                    loginFeedback(error, c);

                    mButtonLogin.setText(R.string.login);
                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }
}

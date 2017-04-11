package com.sharpdroid.registroelettronico.Activities;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.sharpdroid.registroelettronico.API.SpiaggiariApiClient;
import com.sharpdroid.registroelettronico.Databases.RegistroDB;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.Utils.DeviceUuidFactory;

import org.apache.commons.lang3.text.WordUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sharpdroid.registroelettronico.Utils.Metodi.getThemeTextColorSecondary;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.mail)
    TextInputEditText mEditTextMail;
    @BindView(R.id.password)
    TextInputEditText mEditTextPassword;
    @BindView(R.id.login_btn)
    Button mButtonLogin;
    RegistroDB db;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        ButterKnife.bind(this);

        db = new RegistroDB(this);
        Drawable p = AppCompatResources.getDrawable(this, R.drawable.ic_person);
        Drawable l = AppCompatResources.getDrawable(this, R.drawable.ic_password);
        if (p != null) {
            p.setColorFilter(getThemeTextColorSecondary(this), PorterDuff.Mode.SRC_IN);
        }
        if (l != null) {
            l.setColorFilter(getThemeTextColorSecondary(this), PorterDuff.Mode.SRC_IN);
        }
        mEditTextMail.setCompoundDrawablesWithIntrinsicBounds(p, null, null, null);
        mEditTextPassword.setCompoundDrawablesWithIntrinsicBounds(l, null, null, null);

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

    private void Login() {
        String mEmail = mEditTextMail.getText().toString();
        String mPassword = mEditTextPassword.getText().toString();

        mEditTextMail.setEnabled(false);
        mEditTextPassword.setEnabled(false);
        mButtonLogin.setEnabled(false);
        mButtonLogin.setText(R.string.caricamento);

        new SpiaggiariApiClient(this).postLogin(mEmail, mPassword, new DeviceUuidFactory(this).getDeviceUuid().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(login -> {
                    SharedPreferences settings = getSharedPreferences("REGISTRO", MODE_PRIVATE);
                    // Writing data to SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("name", WordUtils.capitalizeFully(login.getName()).trim());
                    editor.apply();

                    if (!db.isUserLogged(mEmail)) {
                        RegistroDB db = new RegistroDB(this);
                        db.addProfile(new ProfileDrawerItem().withName(WordUtils.capitalizeFully(login.getName())).withEmail(mEmail));
                        db.close();
                    }

                    mButtonLogin.setText(R.string.login_riuscito);
                    Toast.makeText(this, R.string.login_msg, Toast.LENGTH_SHORT).show();
                    loggedIn = true;
                    finish();
                }, error -> {
                    error.printStackTrace();
                    mButtonLogin.setText(R.string.login);
                    Toast.makeText(this, R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}

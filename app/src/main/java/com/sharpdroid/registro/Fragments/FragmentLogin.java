package com.sharpdroid.registro.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.Interfaces.Login;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.DeviceUuidFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class FragmentLogin extends SlideFragment {

    @BindView(R.id.mail)
    EditText mEditTextMail;
    @BindView(R.id.password)
    EditText mEditTextPassword;
    @BindView(R.id.login_btn)
    Button mButtonLogin;

    private String mEmail;
    private String mPassword;
    private boolean loggedIn = false;
    private Context mContext;

    private final Runnable LoginRunnable = new Runnable() {
        public void run() {
            RequestParams params = new RequestParams();

            params.put("login", mEmail);
            params.put("password", mPassword);
            params.put("key", new DeviceUuidFactory(mContext).getDeviceUuid().toString());

            RESTFulAPI.post(mContext, RESTFulAPI.LOGIN_URL, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mButtonLogin.setText(R.string.login);
                    Toast.makeText(mContext, R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Login login = new Gson().fromJson(responseString, Login.class);

                    SharedPreferences settings = mContext.getSharedPreferences("REGISTRO", MODE_PRIVATE);

                    // Writing data to SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("name", login.getName());
                    editor.apply();

                    mButtonLogin.setText(R.string.login_riuscito);
                    Toast.makeText(mContext, R.string.login_msg, Toast.LENGTH_SHORT).show();

                    loggedIn = true;
                    updateNavigation();
                }
            });
        }
    };

    public FragmentLogin() {
        // Required empty public constructor
    }

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, root);

        mEditTextMail.setEnabled(!loggedIn);
        mEditTextPassword.setEnabled(!loggedIn);
        mButtonLogin.setEnabled(!loggedIn);

        mEditTextMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        mButtonLogin.setOnClickListener(v -> {
            mEmail = mEditTextMail.getText().toString();
            mPassword = mEditTextPassword.getText().toString();

            mEditTextMail.setEnabled(false);
            mEditTextPassword.setEnabled(false);
            mButtonLogin.setEnabled(false);
            mButtonLogin.setText(R.string.caricamento);

            new Handler().post(LoginRunnable);
        });

        return root;
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }
}
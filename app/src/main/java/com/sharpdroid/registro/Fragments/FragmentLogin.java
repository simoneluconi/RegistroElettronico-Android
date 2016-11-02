package com.sharpdroid.registro.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.R;
import com.sharpdroid.registro.Utils.DeviceUuidFactory;

import cz.msebera.android.httpclient.Header;

public class FragmentLogin extends SlideFragment {

    private EditText mEditTextMail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private String mEmail;
    private String mPassword;

    private boolean loggedIn = false;

    public FragmentLogin() {
        // Required empty public constructor
    }

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mEditTextMail = (EditText) root.findViewById(R.id.mail);
        mEditTextPassword = (EditText) root.findViewById(R.id.password);
        mButtonLogin = (Button) root.findViewById(R.id.login_btn);

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

            new Handler().post(Login);
        });

        return root;
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    private final Runnable Login = new Runnable() {
        public void run() {
            RequestParams params = new RequestParams();

            params.put("login", mEmail);
            params.put("password", mPassword);
            params.put("key", new DeviceUuidFactory(getContext()).getDeviceUuid().toString());

            RESTFulAPI.post(getContext(), RESTFulAPI.LOGIN_URL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getContext(), R.string.login_msg, Toast.LENGTH_SHORT).show();

                    loggedIn = true;
                    updateNavigation();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mButtonLogin.setText(R.string.login);
                    Toast.makeText(getContext(), R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                }
            });
        }
    };
}
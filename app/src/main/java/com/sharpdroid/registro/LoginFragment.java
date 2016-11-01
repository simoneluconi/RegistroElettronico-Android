package com.sharpdroid.registro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

public class LoginFragment extends SlideFragment {

    private EditText mEditTextMail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private String mEmail;
    private String mPassword;

    private boolean loggedIn = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
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

            Handler handler = new Handler();
            handler.post(Login);
        });

        return root;
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    final Runnable Login = new Runnable() {
        public void run() {
            String url = "https://api.daniele.ml/login";

            AsyncHttpClient client = new AsyncHttpClient();
            PersistentCookieStore myCookieStore = new PersistentCookieStore(getContext());
            client.setCookieStore(myCookieStore);

            RequestParams params = new RequestParams();

            params.put("login", mEmail);
            params.put("password", mPassword);
            params.put("key", new DeviceUuidFactory(getContext()).getDeviceUuid().toString());

            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mButtonLogin.setText(R.string.login);
                    Toast.makeText(getContext(), R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                    mEditTextMail.setEnabled(true);
                    mEditTextPassword.setEnabled(true);
                    mButtonLogin.setEnabled(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Toast.makeText(getContext(), R.string.login_msg, Toast.LENGTH_SHORT).show();

                    loggedIn = true;
                    updateNavigation();
                }
            });
        }
    };
}
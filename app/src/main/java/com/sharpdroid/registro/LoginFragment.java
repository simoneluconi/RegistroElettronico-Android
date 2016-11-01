package com.sharpdroid.registro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class LoginFragment extends SlideFragment {
    private final String api_login_url = "https://api.daniele.ml/login";

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

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEditTextMail.getText().toString();
                mPassword = mEditTextPassword.getText().toString();

                mEditTextMail.setEnabled(false);
                mEditTextPassword.setEnabled(false);
                mButtonLogin.setEnabled(false);
                mButtonLogin.setText(R.string.caricamento);

                new UserLoginTask(mEmail, mPassword).execute();
            }
        });

        return root;
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @WorkerThread
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(api_login_url);
                conn = (HttpURLConnection) url.openConnection();

                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(String.format("login=%1$s&password=%2$s&key=%3$s", mEmail, mPassword, UUID.randomUUID().toString()));
                out.close();

                return conn.getResponseCode() == HttpsURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return true;
        }

        @UiThread
        @Override
        protected void onPostExecute(Boolean loggato) {
            if (loggato) {
                Toast.makeText(getContext(), R.string.login_msg, Toast.LENGTH_SHORT).show();

                loggedIn = true;
                updateNavigation();
            } else {
                mButtonLogin.setText(R.string.login);
                Toast.makeText(getContext(), R.string.login_msg_failer, Toast.LENGTH_SHORT).show();

                mEditTextMail.setEnabled(true);
                mEditTextPassword.setEnabled(true);
                mButtonLogin.setEnabled(true);
            }
        }
    }
}
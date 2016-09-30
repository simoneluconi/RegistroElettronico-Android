package com.sharpdroid.registro;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

public class LoginFragment extends SlideFragment {
    private EditText mail;
    private EditText password;
    private Button loginbtn;

    private boolean loggedIn = false;

    private Handler loginHandler = new Handler();

    private Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            loginbtn.setText(R.string.login);
            Toast.makeText(getContext(), R.string.login_msg, Toast.LENGTH_SHORT).show();

            loggedIn = true;
            updateNavigation();
        }
    };

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

        mail = (EditText) root.findViewById(R.id.mail);
        password = (EditText) root.findViewById(R.id.password);
        loginbtn = (Button) root.findViewById(R.id.login_btn);

        mail.setEnabled(!loggedIn);
        password.setEnabled(!loggedIn);
        loginbtn.setEnabled(!loggedIn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setEnabled(false);
                loginbtn.setEnabled(false);
                loginbtn.setText(R.string.caricamento);

            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        loginHandler.removeCallbacks(loginRunnable);
        super.onDestroy();
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }
}


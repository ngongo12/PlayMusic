package com.ps14237;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.User;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    TextView tvReset, tvOption;
    EditText edEmail, edPassword;
    Button btnLogin;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvOption = findViewById(R.id.tvOption);
        tvReset = findViewById(R.id.tvReset);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);

        pref = getSharedPreferences("user", 0);
        edEmail.setText(pref.getString(Constants.EMAIL,""));
        edPassword.setText(pref.getString(Constants.PASSWORD,""));

        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeScreen(RegisterActivity.class);
            }
        });
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressLogin();
            }
        });

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RequestResetPasswordActivity.class));
            }
        });
    }

    private void progressLogin() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        if(isValidate(email, password)){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API api = retrofit.create(API.class);
            User user = new User(email, password);
            ServerRequest request = new ServerRequest();
            request.setOperation(Constants.LOGIN);
            request.setData(user);
            Call<ServerResponse> response = api.userOperation(request);
            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ServerResponse resp = response.body();
                    Log.d("Register", "onResponse: " + response.toString());
                    Toast.makeText(getApplicationContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                    if (resp.getResult().equals("true")){
                        User userResp = (User) resp.getUser();

                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString(Constants.EMAIL, email);
                        editor.putString(Constants.NAME, userResp.getName());
                        editor.putString(Constants.UID, userResp.getId());

                        editor.putString(Constants.PASSWORD, password);
                        editor.apply();

                        gotoMainActivity();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Log.d("Register", "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void changeScreen(Class cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }

    private boolean isValidate(String email, String password) {
        if(email.isEmpty()){
            Toast.makeText(this, "Hãy nhập email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.isEmpty()){
            Toast.makeText(this, "Hãy nhập password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6){
            Toast.makeText(this, "Mật khẩu phải lớn hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
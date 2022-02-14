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

public class RegisterActivity extends AppCompatActivity {
    TextView tvOption;
    EditText edEmail, edPassword, edFullname;
    Button btnRegister;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvOption = findViewById(R.id.tvOption);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnRegister = findViewById(R.id.btnRegister);
        edFullname = findViewById(R.id.edFullname);

        pref = getSharedPreferences("user", 0);
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressRegister();
            }
        });
    }

    private void progressRegister() {
        String name = edFullname.getText().toString();
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        if(isValidate(name, email, password)){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            API api = retrofit.create(API.class);
            User user = new User(email, password, name);
            ServerRequest request = new ServerRequest();
            request.setOperation(Constants.REGISTER);
            request.setData(user);
            Call<ServerResponse> response = api.userOperation(request);
            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ServerResponse resp = response.body();
                    Log.d("Register", "onResponse: " + response.toString());
                    Toast.makeText(getApplicationContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                    if (resp.getResult().equals("true")){
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString(Constants.EMAIL, email);
                        editor.putString(Constants.NAME, name);

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

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidate(String name, String email, String password) {
        if (name.isEmpty())
        {
            Toast.makeText(this, "Hãy nhập họ tên", Toast.LENGTH_SHORT).show();
            return false;
        }
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
}
package com.ps14237;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edCode, edPassword;
    TextView tvTime, tvBack;
    Button btnReset;
    SharedPreferences pref;

    int clock = 120;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edCode = findViewById(R.id.edCode);
        edPassword = findViewById(R.id.edPassword);
        tvTime = findViewById(R.id.tvTime);
        tvBack = findViewById(R.id.tvBack);
        btnReset = findViewById(R.id.btnChange);
        pref = getSharedPreferences("user", 0);

        countDownTimer = new CountDownTimer(120*1000, 1000) {
            @Override
            public void onTick(long l) {
                tvTime.setText("Time remaining: " + l/1000 + "s");
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(ResetPasswordActivity.this, RequestResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        };
        countDownTimer.start();

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, RequestResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edCode.getText().toString();
                String password = edPassword.getText().toString();
                String email = pref.getString(Constants.EMAIL, "");
                resetProgress(code, password, email);
            }
        });
    }
    private void resetProgress(String code, String password, String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET);
        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        request.setData(user);
        Call<ServerResponse> response = api.userOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Reset Password",response.toString());
                Toast.makeText(getApplicationContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                if (resp.getResult().equals(Constants.SUCCESS)){
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Reset Password",t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
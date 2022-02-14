package com.ps14237;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.User;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestResetPasswordActivity extends AppCompatActivity {

    EditText edEmail;
    Button btnReset;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset_password);

        edEmail = findViewById(R.id.edEmail);
        btnReset = findViewById(R.id.btnReset);
        pref = getSharedPreferences("user", 0);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString();
                sendEmailProgress(email);
                btnReset.setEnabled(false);
            }
        });
    }

    private void sendEmailProgress(String email) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REQUEST_RESET);
        User user = new User();
        user.setEmail(email);
        request.setData(user);
        Call<ServerResponse> response = api.userOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Request reset",response.toString());
                Toast.makeText(getApplicationContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                if (resp.getResult().equals(Constants.SUCCESS)){
                    btnReset.setEnabled(true);
                    SharedPreferences.Editor editor = pref.edit();

                    //Lưu email vừa gửi vào shared
                    editor.putString(Constants.EMAIL, edEmail.getText().toString());

                    editor.apply();
                    Intent intent = new Intent(RequestResetPasswordActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Request reset",t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "failed" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                btnReset.setEnabled(true);
            }
        });
    }
}
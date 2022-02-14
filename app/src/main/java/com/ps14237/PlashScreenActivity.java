package com.ps14237;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class PlashScreenActivity extends AppCompatActivity {
    TextView tvName;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plash_screen);
        pref = getSharedPreferences("user", 0);
        //full screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //end full screen
        
        tvName = findViewById(R.id.tvName);
        Shader shader = new LinearGradient(0, 0, 0,tvName.getTextSize(),
                new int[]{
                        Color.parseColor("#5F17AB"),
                        Color.parseColor("#135895"),
                        Color.parseColor("#135895"),
                        Color.parseColor("#5741C1"),
                        Color.parseColor("#7429C4"),
                        Color.parseColor("#5F17AB"),
                },
                new float[] {
                        0, 0.2f ,0.4f, 0.6f, 0.8f,1 },
                Shader.TileMode.CLAMP);
        tvName.getPaint().setShader(shader);

        progressLogin();
    }

    private void progressLogin() {
        String email = pref.getString(Constants.EMAIL, "");
        String password = pref.getString(Constants.PASSWORD, "");

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

                    changeScreen(MainActivity.class);

                }
                else{
                    changeScreen(LoginActivity.class);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Register", "onFailure: " + t.getMessage());
                changeScreen(LoginActivity.class);
            }
        });
    }

    private void changeScreen(Class cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }
}
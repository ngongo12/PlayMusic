package com.ps14237.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.ps14237.LoginActivity;
import com.ps14237.R;
import com.ps14237.adapter.SettingMenuAdapter;
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

public class SettingFragment extends Fragment {

    ListView lv;
    TextView tvUserName, tvUploaded;
    TextView tvUsername, tvEmail, tvCancel, tvOK;
    EditText edPassword, edOldPassword;
    Dialog dialog;
    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = getView().findViewById(R.id.lv);
        tvUserName = getView().findViewById(R.id.tvUserName);
        tvUploaded = getView().findViewById(R.id.tvUploaded);

        pref = getActivity().getSharedPreferences("user", 0);
        getUserInfo();

        lv.setAdapter(new SettingMenuAdapter(getContext()));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 5:{
                        logout();
                        break;
                    }
                    case 4:{
                        changePassword();
                        break;
                    }
                }
            }
        });
    }

    private void getUserInfo() {
        tvUserName.setText(pref.getString(Constants.NAME, ""));
        tvUploaded.setText(pref.getString(Constants.EMAIL, ""));
    }

    private void changePassword() {
        openDialog();
    }

    private void openDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
        dialog = new Dialog(getContext());
//        View view = inflater.inflate(R.layout.change_password_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.change_password_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        edPassword = dialog.findViewById(R.id.edPassword);
        edOldPassword = dialog.findViewById(R.id.edOldPassword);
        tvCancel = dialog.findViewById(R.id.tvCancel);
        tvOK = dialog.findViewById(R.id.tvOK);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = edOldPassword.getText().toString();
                String newPass = edPassword.getText().toString();
                changePasswordProgress(old, newPass);

            }
        });

        dialog.show();

    }

    private void changePasswordProgress(String oldPass, String newPass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD);
        String email = pref.getString(Constants.EMAIL, "");
        User user = new User();
        user.setEmail(email);
        user.setNew_password(newPass);
        user.setOld_password(oldPass);
        request.setData(user);
        Call<ServerResponse> response = api.userOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Change password",response.toString());
                Toast.makeText(getContext(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Change password",resp.getResult());
                if(resp.getResult().equals("true"))
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Change password","failed");
                Log.d("Change password",t.getLocalizedMessage());
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void logout() {
        Snackbar snackbar = Snackbar.make(getView(), "Bạn muốn thoát?", Snackbar.LENGTH_LONG);
        snackbar.setAction("THOÁT", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        snackbar.show();
    }
}

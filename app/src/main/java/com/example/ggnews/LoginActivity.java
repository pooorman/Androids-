package com.example.ggnews;



import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
  private Boolean bPwdSwitch = false;
  private ImageView ivPwdSwitch;

  private TextView tvSignUp;
  private EditText etPwd;
  private EditText etAccount;
  private CheckBox cbRememberPwd;

  private okhttp3.Callback signup = new okhttp3.Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
      Log.e(TAG, "Failed to connect server!");
      e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response)
      throws IOException {
      //接口是看内部的code,解析他的code
      final String body = response.body().string();
      Gson gson = new Gson();
      Type jsonType =
        new TypeToken<BaseResponse<Objects>>() {}.getType();
        BaseResponse<Objects> Response =
        gson.fromJson(body, jsonType);

      if (Response.getCode()==200) {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new  AlertDialog.Builder(LoginActivity.this)
              .setTitle("注册提示")
              .setMessage("注册成功" )
              .setPositiveButton("确定" ,  null )
              .show();

          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new AlertDialog.Builder(LoginActivity.this)
              .setTitle("注册提示")
              .setMessage(Response.getMsg())
              .setPositiveButton("确定", null)
              .show();
          }
        });
      }
    }
  };

  private okhttp3.Callback login = new okhttp3.Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
      Log.e(TAG, "Failed to connect server!");
      e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response)
      throws IOException {
      //接口是看内部的code,解析他的code
      final String body = response.body().string();
      Gson gson = new Gson();
      Type jsonType =
        new TypeToken<BaseResponse<Objects>>() {}.getType();
      BaseResponse<Objects> Response =
        gson.fromJson(body, jsonType);

      if (Response.getCode()==200) {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new  AlertDialog.Builder(LoginActivity.this)
              .setTitle("登录提示")
              .setMessage("登录成功" )
              .setPositiveButton("确定" ,  null )
              .show();

            //登录成功跳转
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new AlertDialog.Builder(LoginActivity.this)
              .setTitle("登录提示")
              .setMessage(Response.getMsg())
              .setPositiveButton("确定", null)
              .show();
          }
        });
      }
    }
  };


  public void showLoginDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
    builder.setTitle("注册");

    LayoutInflater inflater = getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.fragment_login_dialog, null);
    builder.setView(dialogView);

    EditText usernameEditText = dialogView.findViewById(R.id.iv_username);
    EditText passwordEditText = dialogView.findViewById(R.id.iv_password);

    builder.setPositiveButton("Sign in",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            //发注册请求
            new Thread(() -> {

              FormBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();

              //请求路径
              Request request = new Request.Builder()
                .addHeader("appId","37baffe1646a4411a338eb820a131176")
                .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
                .url(Constants.SERVER_URL2 + "user/register")
                .post(formBody).build();

              try {
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(signup);
              } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
                ex.printStackTrace();
              }
            }).start();
          }
        })
      .setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();

      }
    });
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //获取登录相关的组件
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
    etPwd = findViewById(R.id.ed_pwd);
    etAccount = findViewById(R.id.et_account);
    Button btLogin = findViewById(R.id.bt_login);
    cbRememberPwd=findViewById(R.id.cb_remember_pwd);
    tvSignUp = findViewById(R.id.tv_sign_up);
    ivPwdSwitch.setOnClickListener(this);
    btLogin.setOnClickListener(this);


    //读取缓存文件
    String spFileName = getResources().getString(R.string.shared_preferences_file_name);
    String accountKey = getResources().getString(R.string.login_account_name);
    String passwordKey = getResources().getString(R.string.login_password);
    String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
    SharedPreferences spFile = getSharedPreferences(spFileName ,MODE_PRIVATE);
    String account = spFile.getString(accountKey , null);
    String password = spFile.getString(passwordKey , null);
    Boolean rememberPassword=spFile.getBoolean(rememberPasswordKey ,false);
    if(account!=null&& !TextUtils.isEmpty(account)){
      etAccount.setText(account);
    }
    if(password != null && !TextUtils.isEmpty(password)){
      etPwd.setText(password);
    }
    cbRememberPwd.setChecked(rememberPassword);



    //创建弹窗
    View.OnClickListener onClickListener = v -> showLoginDialog();
    tvSignUp.setOnClickListener(onClickListener);




  }

  @Override
  public void onClick(View view) {
    int viewId=view.getId();
    if(viewId==R.id.iv_pwd_switch){
      bPwdSwitch = !bPwdSwitch;
      if(bPwdSwitch){
        ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_24);
        etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
      }else{
        ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_off_24);
        etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        etPwd.setTypeface(Typeface.DEFAULT);
      }
    }else if(viewId==R.id.bt_login){
      String spFileName = getResources().getString(R.string.shared_preferences_file_name);
      String accountKey = getResources().getString(R.string.login_account_name);
      String passwordKey = getResources().getString(R.string.login_password);
      String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
      SharedPreferences spFile = getSharedPreferences(spFileName , Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = spFile.edit();
      if(cbRememberPwd.isChecked()){
        String password = etPwd.getText().toString();
        String account = etAccount.getText().toString();
        editor.putString(accountKey , account);
        editor.putString(passwordKey , password);
        editor.putBoolean(rememberPasswordKey , true);
        editor.apply();
      }else{
        editor.remove(accountKey);
        editor.remove(passwordKey);
        editor.remove(rememberPasswordKey);
        editor.apply();
      }

      //发送登录的请求
      String password = etPwd.getText().toString();
      String username = etAccount.getText().toString();

      new Thread(() -> {

        FormBody formBody = new FormBody.Builder()
          .add("username",username)
          .add("password",password)
          .build();

        //请求路径
        Request request = new Request.Builder()
          .addHeader("appId","37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
          .url(Constants.SERVER_URL2 + "user/login")
          .post(formBody).build();

        try {
          OkHttpClient client = new OkHttpClient();
          client.newCall(request).enqueue(login);
        } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
          ex.printStackTrace();
        }
      }).start();



//      Intent intent = new Intent(this, MainActivity.class);
//      startActivity(intent);
    }

  }
}

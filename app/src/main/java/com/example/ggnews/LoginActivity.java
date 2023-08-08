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
      if (response.isSuccessful()) {
        final String body = response.body().string();

        runOnUiThread(new Runnable() {
          @Override
          public void run() {

            Gson gson = new Gson();
            Type jsonType = new TypeToken<BaseResponse<List<News>>>() {}.getType();
//            获取响应结果
            BaseResponse<List<News>> newsListResponse = gson.fromJson(body, jsonType);

          }
        });
      } else {
        //失败的处理
      }
    }
  };

  public void showLoginDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

            // Process the username and password
            // ...
          }
        })
      .setNegativeButton("Cancel", null);

    AlertDialog dialog = builder.create();
    dialog.show();
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


    //注册注册按钮
//    View.OnClickListener onClickListener = v -> new Thread(() -> {
//
//      FormBody formBody = new FormBody.Builder()
//        .add("username","username")
//        .add("password","password")
//        .build();
//
//      //请求路径
//      Request request = new Request.Builder()
//        .addHeader("appId","37baffe1646a4411a338eb820a131176")
//        .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
//        .url(Constants.SERVER_URL2 + "user/register")
//        .post(formBody).build();
//
//      try {
//        OkHttpClient client = new OkHttpClient();
//        client.newCall(request).enqueue(signup);
//      } catch (NetworkOnMainThreadException ex) {
////            主线程网络错误
//        ex.printStackTrace();
//      }
//    }).start();
//    tvSignUp.setOnClickListener(onClickListener);
    // 在点击注册按钮的事件处理程序中


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
      Intent intent = new Intent(this, MainActivity.class);
      startActivity(intent);
    }

  }
}

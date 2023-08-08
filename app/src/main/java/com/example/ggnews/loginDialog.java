//package com.example.ggnews;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//import androidx.fragment.app.DialogFragment;
//
//public class loginDialog extends DialogFragment {
//
//  @Override
//  public Dialog onCreateDialog(Bundle savedInstanceState)
//  {
//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    builder.setTitle("注册");
//
//    LayoutInflater inflater = getLayoutInflater();
//    View dialogView = inflater.inflate(R.layout.fragment_login_dialog, null);
//    builder.setView(dialogView);
//
//    EditText usernameEditText = dialogView.findViewById(R.id.iv_username);
//    EditText passwordEditText = dialogView.findViewById(R.id.iv_password);
//
//    builder.setView(dialogView).setPositiveButton("Sign in",
//        new DialogInterface.OnClickListener()
//        {
//          @Override
//          public void onClick(DialogInterface dialog, int id)
//          {
//            String username = usernameEditText.getText().toString();
//            String password = passwordEditText.getText().toString();
//
//            // 将用户名和密码传递给LoginActivity
////            Intent intent = new Intent(this, LoginActivity.class);
////            intent.putExtra("username", username);
////            intent.putExtra("password", password);
////            startActivity(intent);
//          }
//        }).setNegativeButton("Cancel", null);
//    return builder.create();
//  }
//
//}

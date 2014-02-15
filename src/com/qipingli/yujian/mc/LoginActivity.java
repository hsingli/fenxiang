package com.qipingli.yujian.mc;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        
    }

    public void login_mainyujian(View v) {
    	if((mUser.getText().toString().trim().length()> 0) && "123".equals(mPassword.getText().toString().trim()))   //判断 帐号和密码
        {
             Intent intent = new Intent();
             Bundle bundle = new Bundle();
             bundle.putString("username", mUser.getText().toString().trim());
             intent.putExtras(bundle);
             intent.setClass(LoginActivity.this,MainYujianActivity.class);
             startActivity(intent);
             //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
          }
        else if("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //判断 帐号和密码
        {
        	new AlertDialog.Builder(LoginActivity.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录错误")
			.setMessage("微信帐号或者密码不能为空，\n请输入后再登录！")
			.create().show();
         }
        else{
           
        	new AlertDialog.Builder(LoginActivity.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录失败")
			.setMessage("微信帐号或者密码不正确，\n请检查后重新输入！")
			.create().show();
        }
    	
    	//登录按钮
    	/*
      	Intent intent = new Intent();
		intent.setClass(Login.this,Whatsnew.class);
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
		this.finish();*/
      }  
    public void login_back(View v) {     //标题栏 返回按钮
      	this.finish();
      }  
    public void login_pw(View v) {     //忘记密码按钮
    	Uri uri = Uri.parse("http://qipingli.vicp.net/Photo/login.jsp"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
      }  
}

package com.example.courseapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登录页。就是报告 3.2.1 贴的那段代码。
 */
public class LoginActivity extends Activity {

    EditText etUser, etPwd;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = (EditText) findViewById(R.id.etUser);
        etPwd = (EditText) findViewById(R.id.etPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etUser.getText().toString().trim();
                String pwd  = etPwd.getText().toString().trim();
                if (name.length() == 0 || pwd.length() == 0) {
                    Toast.makeText(LoginActivity.this, "账号密码不能空着",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                DBHelper h = new DBHelper(LoginActivity.this);
                SQLiteDatabase db = h.getReadableDatabase();
                // 按账号和密码一起查，查得到就算登录成功
                Cursor cur = db.rawQuery(
                        "select * from user where account=? and password=?",
                        new String[]{name, pwd});
                if (cur.getCount() > 0) {
                    Toast.makeText(LoginActivity.this, "登录成功",
                            Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.putExtra("account", name);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码错了",
                            Toast.LENGTH_SHORT).show();
                }
                cur.close();
                db.close();
            }
        });
    }
}

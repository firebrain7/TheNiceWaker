package com.example.exp.sleep.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.exp.sleep.Fragment.HomePageActivity;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Tools.OrderDBHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// 登录功能暂时用SharePreference实现 之后再迁移到数据库

public class LoginActivity extends AppCompatActivity {

    private static OrderDBHelper ordersDBHelper ;
    //private int boxSel;  //本来想用boolean  但是写入不行

    public CheckBox cb_Remember;//=(CheckBox)findViewById(R.id.cb_remember);
    public EditText et_UserName;//=(EditText)findViewById(R.id.et_UserName);
    public EditText et_Password;//=(EditText)findViewById(R.id.et_Password);
    private Dialog dlg2=null;
    //保存密码
    public void SavePassword() {
/*
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            //Todo : Encryption
            out = openFileOutput("sets", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            int boxSel = cb_Remember.isChecked() ? 1 : 0;
            writer.write(boxSel);
            if (boxSel == 1) {
                writer.newLine();
                //writer.write(et_UserName.toString().length());
                writer.write(et_UserName.getText().toString());//暂且采用明文存储，之后可以改为密文
                writer.newLine();
                //writer.write(et_Password.toString().length());
                writer.write(et_Password.getText().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        SharedPreferences preferences=getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        int boxSel = cb_Remember.isChecked() ? 1 : 0;
        if (boxSel == 1) {
            String strSel="1";
            String user=    et_UserName.getText().toString();
            String password=et_Password.getText().toString();
            editor.putString("boxsel", strSel);
            editor.putString("user", user);
            editor.putString("password",password);
            editor.commit();
        }

    }

    //读取密码
    public void Login(View view) {
        //待添加：用户认证(数据库)

        SQLiteDatabase db = ordersDBHelper.getReadableDatabase();
        String[] username = {""};
        EditText et_UserName = findViewById(R.id.et_UserName);
        EditText et_Password = findViewById(R.id.et_Password);
        Dialog dlg=null;
        username[0] = et_UserName.getText().toString();
        if (username[0].length() == 0) {
            Log.i("login", "Empty text");
            dlg=new AlertDialog.Builder(this).setTitle(null).setMessage("UserName is empty, please check").setPositiveButton("ok", null).show();
            return;
        }
        if (et_Password.getText().toString().length() == 0) {
            Log.i("login", "Empty text2");
            dlg=new AlertDialog.Builder(this).setTitle(null).setMessage("Password is empty, please check").setPositiveButton("ok", null).show();
            return;
        }

        Cursor cursor = db.query(OrderDBHelper.TABLE_NAME, new String[]{"Password"}, "UserName = ?", username, null, null, null);

        int count = cursor.getCount();
        if (count == 0) {
            Log.i("login", "No User");
            dlg=new AlertDialog.Builder(this).setTitle("Failed").setMessage("cannot find the user").setPositiveButton("ok", null).show();
            return;
        }
        cursor.moveToFirst();
        String str = cursor.getString(0);
        if (et_Password.getText().toString().equals(str)) {
            Log.i("login", "Login: succeed");
            SavePassword();
            startActivity(new Intent(this,MenuActivity.class));
            if(dlg!=null)          dlg.dismiss();
            finish();
            return;
        } else {
            Log.i("login", "Login: fail");
            dlg=new AlertDialog.Builder(this).setTitle("Failed").setMessage("Password wrong").setPositiveButton("ok", null).show();
            return;
        }
    }

    public void Register(View view){

        setContentView(R.layout.activity_register);

    }

    public void Register2(View view) {
        //SQLiteDatabase db = ordersDBHelper.getReadableDatabase();

        EditText et_UserName2 = findViewById(R.id.et_UserName2);
        EditText et_Password2 = findViewById(R.id.et_Password2);
        String[] username = {""};
        username[0] = et_UserName2.getText().toString();
        /*Cursor cursor=db.query(OrderDBHelper.TABLE_NAME,new String[]{"COUNT(UserName)"},"UserName= ?",username,null,null,null);
        if(cursor.getCount()>0){
            Log.i("register", "Register: fail");
            return;
        }*/

        SQLiteDatabase db = ordersDBHelper.getWritableDatabase();
        try {

            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("UserName", et_UserName2.getText().toString());
            contentValues.put("Password", et_Password2.getText().toString());
            //db.insert(OrderDBHelper.TABLE_NAME,null, contentValues);
            db.insertOrThrow(OrderDBHelper.TABLE_NAME, null, contentValues);

            db.setTransactionSuccessful();
            Log.i("register", "Register2: succeed,user:" + et_UserName2.getText().toString() + "  \tpassword:" + et_Password2.getText().toString());
            setContentView(R.layout.activity_login);
            et_UserName.setText(et_UserName2.getText().toString());
            et_Password.setText(et_Password2.getText().toString());
            dlg2=new AlertDialog.Builder(this).setTitle("Congratulations!").setMessage("Your account has been created.").setPositiveButton("Ok", null).show();
        } catch (SQLiteConstraintException e) {
            Log.i("register", "Register2: fail");
            dlg2=new AlertDialog.Builder(this).setTitle("Attention").setMessage("Username already taken, please try something else.").setPositiveButton("Ok", null).show();
        } finally {
            db.endTransaction();
        }
    }

    public void LoadPassword() {
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        String strSel=preferences.getString("boxsel","0");
        String user=preferences.getString("user", "defaultname");
        String password=preferences.getString("password", "default");
        if (strSel.equals("1")) {

            cb_Remember.setChecked(true);

            et_UserName.setText(user);

            et_Password.setText(password);
            //boxSel=1;

        } else {
            cb_Remember.setChecked(false);
            //boxSel=0;
        }
        /*
        FileInputStream in;
        BufferedReader reader = null;
        int Sel = 0;
        try {
            in = openFileInput("sets");
            reader = new BufferedReader(new InputStreamReader(in));
            Sel = reader.read();
            if (Sel == 1) {
                reader.read();
                cb_Remember.setChecked(true);
                String str = reader.readLine();
                et_UserName.setText(str);
                str = reader.readLine();
                et_Password.setText(str);
                //boxSel=1;

            } else {
                cb_Remember.setChecked(false);
                //boxSel=0;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    public void Back2(View view) {
        if(dlg2!=null)
            dlg2.dismiss();
        setContentView(R.layout.activity_login);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //应先加载欢迎界面，进行数据处理
        setContentView(R.layout.activity_login);
        cb_Remember=(CheckBox)findViewById(R.id.cb_Remember);
        et_UserName=(EditText)findViewById(R.id.et_UserName);
        et_Password=(EditText)findViewById(R.id.et_Password);
        ordersDBHelper = new OrderDBHelper(this);
        LoadPassword();

    }


}



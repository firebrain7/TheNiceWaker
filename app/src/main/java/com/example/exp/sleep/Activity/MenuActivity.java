package com.example.exp.sleep.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.exp.sleep.Fragment.HomePageActivity;
import com.example.exp.sleep.Fragment.MainActivity;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Tools.FileHandler;

public class MenuActivity extends AppCompatActivity implements  BottomNavigationBar.OnTabSelectedListener{
    BottomNavigationBar mBottomNavigationBar;
    private HomePageActivity mFragmentOne;
    private MainActivity mFragmentTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initLayout();
        InitPermission();
//        SetupUI();
    }

    public void  InitPermission(){
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECORD_AUDIO", "packageName"));
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
            Toast.makeText(this,"If you want to know your sleep condtion better,please open the permission of RECORD_AUDIO",Toast.LENGTH_LONG).show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }
    }

    public void SetupUI(){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected  void  initLayout(){
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        // setting for badgeItem
        int FileNum = FileHandler.listFiles().length;
        BadgeItem badgeItem = new BadgeItem();
        badgeItem.setHideOnSelect(false)
                .setText(FileNum + "")
                .setBackgroundColorResource(R.color.orange)
                .setBorderWidth(0);

        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setBarBackgroundColor(R.color.black);
        mBottomNavigationBar.setActiveColor(R.color.white);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.tab_homepage, R.string.tab_one).setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.datapage, R.string.tab_two).setActiveColorResource(R.color.green).setBadgeItem(badgeItem))
                .setFirstSelectedPosition(0)//设置默认选择item
                .initialise();//初始化

        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mFragmentOne = new HomePageActivity();
        transaction.replace(R.id.ll_content, mFragmentOne).commit();
    }

    @Override
    public void onTabSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position){
            case 0:
                if(mFragmentOne == null){
                    mFragmentOne = new HomePageActivity();
                }
                transaction.replace(R.id.ll_content,mFragmentOne);
                break;
            case 1:
                if(mFragmentTwo == null){
                    mFragmentTwo = new MainActivity();
                }
                transaction.replace(R.id.ll_content,mFragmentTwo);
                break;
            default:
                if (mFragmentOne == null) {
                    mFragmentOne = new HomePageActivity();
                }
                transaction.replace(R.id.ll_content, mFragmentOne);
                break;
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}

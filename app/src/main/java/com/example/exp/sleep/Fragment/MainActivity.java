package com.example.exp.sleep.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.exp.sleep.Activity.SingleNight;
import com.example.exp.sleep.Tools.NightListAdapter;
import com.example.exp.sleep.R;
import com.example.exp.sleep.Tools.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Fragment {

    private NightListAdapter nightListAdapter;
    private Context mContext;
    public static MainActivity instance = null;
    public static MainActivity GetInstance(){
        if(instance == null){
            instance = new MainActivity();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.activity_main,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        if(!isExternalStorageWritable()) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Caution")
                    .setMessage("The storage is not accessable. Please make sure to insert your sd-card and restart the app.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        setupNightList();
    }


    public void setupNightList() {
        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        nightListAdapter = new NightListAdapter(getContext(), android.R.layout.simple_list_item_1, nights);

        final ListView listView = (ListView) getView().findViewById(R.id.nights_list);
        listView.setAdapter(nightListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File file = (File) listView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), SingleNight.class);
                intent.putExtra(SingleNight.EXTRA_FILE, file.getAbsolutePath());
                MainActivity.this.startActivity(intent);
            }
        });
    }

    public void updateNightList() {
        ArrayList<File> nights = new ArrayList<>(Arrays.asList(FileHandler.listFiles()));
        nightListAdapter.clear();
        nightListAdapter.addAll(nights);
        nightListAdapter.notifyDataSetChanged();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}

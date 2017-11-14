package ru.vpcb.btmain;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isStatic = true;
        if(isStatic) {
            setContentView(R.layout.fragment_main);  // static version
        }else {
            setContentView(R.layout.fragment_main_recycler);  // static version
            if(savedInstanceState == null) {

                MainFragment mainFragment = new MainFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .add(R.id.fc_container, mainFragment)
                        .commit();
            }

        }


    }
}

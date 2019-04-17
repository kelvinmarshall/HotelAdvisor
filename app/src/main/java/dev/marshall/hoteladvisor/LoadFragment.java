package dev.marshall.hoteladvisor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Marshall on 31/03/2018.
 */
public class LoadFragment {
    FragmentManager fragmentManager;
    public LoadFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    protected void initializeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
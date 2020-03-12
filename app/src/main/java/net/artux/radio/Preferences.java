package net.artux.radio;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    final static  String filename = "preferences";

    private SharedPreferences preferences;

    public Preferences(Context context){
        preferences = context.getSharedPreferences(filename, 0);
    }

    private SharedPreferences.Editor getEditor(){
        return preferences.edit();
    }
}

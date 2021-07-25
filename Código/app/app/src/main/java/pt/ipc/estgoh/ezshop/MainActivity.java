package pt.ipc.estgoh.ezshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import pt.ipc.estgoh.ezshop.ui.auth.view.AuthenticationActivity;
import pt.ipc.estgoh.ezshop.ui.main.view.HomeActivity;

public class MainActivity extends HomeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        if (!sharedPref.contains("token")) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
    }
}

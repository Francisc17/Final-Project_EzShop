package pt.ipc.estgoh.ezshop.ui.main.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pt.ipc.estgoh.ezshop.MainActivity;
import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.databinding.SettingsActivityBinding;

public class SettingsActivity extends AppCompatActivity {
    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();


        binding.exitBtn.setOnClickListener(v -> {
            final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(getString(R.string.saved_token));
            editor.apply();

            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
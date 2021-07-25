package pt.ipc.estgoh.ezshop.ui.auth.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipc.estgoh.ezshop.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(binding.getRoot().getId(), LoginFragment.class, null)
                .commit();
    }

    public void replaceFragment(final Class aClass) {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.getRoot().getId(), aClass, null)
                .commit();
    }
}

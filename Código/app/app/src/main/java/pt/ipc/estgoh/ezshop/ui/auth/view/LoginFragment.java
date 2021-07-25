package pt.ipc.estgoh.ezshop.ui.auth.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.viewmodel.UserViewModel;
import pt.ipc.estgoh.ezshop.databinding.LoginFragmentBinding;
import pt.ipc.estgoh.ezshop.ui.main.view.HomeActivity;

public class LoginFragment extends Fragment {
    private LoginFragmentBinding binding;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        binding = LoginFragmentBinding.inflate(inflater, container, false);

        binding.createAccountLogin.setOnClickListener(v -> ((AuthenticationActivity) requireActivity()).replaceFragment(RegisterFragment.class));

        binding.btnEnterLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), HomeActivity.class)));

        binding.btnEnterLogin.setOnClickListener(this::login);
        binding.passwordLogin.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.btnEnterLogin.performClick();
                handled = true;
            }
            return handled;
        });


        return binding.getRoot();
    }

    private void login(final View v) {
        boolean error = false;
        final String email = binding.emailLogin.getText().toString().trim();
        final String password = binding.passwordLogin.getText().toString();

        if (email.isEmpty()) {
            binding.emailLogin.setError(getString(R.string.fill_field));
            error = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLogin.setError(getString(R.string.invalid_email));
            error = true;
        }
        if (password.isEmpty() || password.length() < 7) {
            binding.passwordLogin.setError(getString(R.string.fill_password));
            error = true;
        }
        if (!error)
            this.viewModel.login(email, password).observe(getViewLifecycleOwner(), this::handleLoginResponse);
    }

    private void handleLoginResponse(EzResponse<String> authResponse) {
        if (authResponse != null)
            if (authResponse.getCode().equals("401"))
                Toast.makeText(getContext(), R.string.email_or_password_wrong, Toast.LENGTH_SHORT).show();
            else if (authResponse.getCode().equals("200")) {
                this.saveToken(authResponse.getData());
                this.requireContext().startActivity(new Intent(this.getContext(), HomeActivity.class));
                requireActivity().finish();
            }
    }

    private void saveToken(final String token) {
        Context context = requireContext();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.user_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_token), token);
        editor.apply();
    }
}

package pt.ipc.estgoh.ezshop.ui.auth.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.User;
import pt.ipc.estgoh.ezshop.data.viewmodel.UserViewModel;
import pt.ipc.estgoh.ezshop.databinding.RegisterFragmentBinding;
import pt.ipc.estgoh.ezshop.ui.main.view.HomeActivity;

public class RegisterFragment extends Fragment {
    private RegisterFragmentBinding binding;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        binding = RegisterFragmentBinding.inflate(inflater, container, false);

        binding.loginBtn.setOnClickListener(v -> ((AuthenticationActivity) requireActivity()).replaceFragment(LoginFragment.class));

        binding.btnEnterRegister.setOnClickListener(this::register);

        return binding.getRoot();
    }

    private void register(final View v) {
        boolean error = false;
        final String name = binding.nameRegister.getText().toString().trim();
        final String email = binding.emailRegister.getText().toString().trim();
        final String password = binding.passwordRegister.getText().toString();

        if (name.isEmpty()) {
            binding.nameRegister.setError(getString(R.string.fill_field));
            error = true;
        }
        if (email.isEmpty()) {
            binding.emailRegister.setError(getString(R.string.fill_field));
            error = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailRegister.setError(getString(R.string.invalid_email));
            error = true;
        }

        if (password.isEmpty() || password.length() < 7) {
            binding.passwordRegister.setError(getString(R.string.fill_password));
            error = true;
        }

        if (!error)
            this.viewModel.register(new User(name, email, password)).observe(getViewLifecycleOwner(), this::handleRegisterResponse);
    }

    private void handleRegisterResponse(EzResponse<String> authResponse) {
        if (authResponse != null) {
            if (authResponse.getCode().equals("406")) {
                Toast.makeText(getContext(), R.string.email_already_registered, Toast.LENGTH_SHORT).show();
            } else if (authResponse.getCode().equals("201")) {
                this.saveToken(authResponse.getData());
                this.requireContext().startActivity(new Intent(getContext(), HomeActivity.class));
                requireActivity().finish();
            }
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

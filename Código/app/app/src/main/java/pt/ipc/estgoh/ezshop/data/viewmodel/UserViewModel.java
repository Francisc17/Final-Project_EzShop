package pt.ipc.estgoh.ezshop.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.User;
import pt.ipc.estgoh.ezshop.data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext());
    }

    public LiveData<EzResponse<String>> login(final String aEmail, final String aPassword) {
        return this.userRepository.login(aEmail, aPassword);
    }

    public LiveData<EzResponse<String>> register(final User aUser) {
        return this.userRepository.register(aUser);
    }
}


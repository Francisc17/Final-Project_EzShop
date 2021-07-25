package pt.ipc.estgoh.ezshop.ui.list.view;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ShareFragmentBinding;
import pt.ipc.estgoh.ezshop.ui.main.adapter.SharedListAdapter;

public class ShareBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private ShareFragmentBinding binding;
    private ShoppingListsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShareFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingListsViewModel.class);
        final ShoppingList shoppingList = viewModel.getCurrentShoppingList().getValue();

        if (shoppingList == null) {
            Toast.makeText(this.getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            this.dismiss();
            return null;
        }

        this.binding.usersSharedRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.binding.usersSharedRv.setAdapter(new SharedListAdapter(shoppingList.getUsers()));

        this.binding.shareListBtn.setOnClickListener(v -> {
            final String email = this.binding.shareEmailEt.getText().toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                this.binding.shareEmailEt.setError(getString(R.string.invalid_email));
                return;
            }

            viewModel.shareList(shoppingList.getId(), email).observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    switch (response.getCode()) {
                        case "400":
                            Toast.makeText(getContext(), String.format(getString(R.string.list_already_shared_with), email), Toast.LENGTH_SHORT).show();
                            break;
                        case "404":
                            Toast.makeText(getContext(), R.string.email_not_registered, Toast.LENGTH_SHORT).show();
                            break;
                        case "200":
                            shoppingList.setShared(true);
                            this.viewModel.setCurrentShoppingList(shoppingList);
                            this.dismiss();
                            Toast.makeText(getContext(), String.format(getString(R.string.list_shared), email), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        });

        return binding.getRoot();
    }
}

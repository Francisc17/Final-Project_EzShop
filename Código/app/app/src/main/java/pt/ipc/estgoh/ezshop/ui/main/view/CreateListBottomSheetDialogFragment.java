package pt.ipc.estgoh.ezshop.ui.main.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.CreateListFragmentBinding;

public class CreateListBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final int EDIT = 0;
    private static final int CREATE = 1;
    private CreateListFragmentBinding binding;
    private ShoppingListsViewModel viewModel;
    private ShoppingList shoppingList = new ShoppingList();
    private int mode = CREATE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateListFragmentBinding.inflate(inflater, container, false);

        //Get the viewModelProvider from ShoppingListsViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingListsViewModel.class);

        //If user click on save btn call handleSaveClick method
        binding.saveListBtn.setOnClickListener(this::handleSaveClick);

        viewModel.getCurrentShoppingList().observe(this.getViewLifecycleOwner(), list -> {
            if (list != null) {
                this.shoppingList = list;
                this.mode = EDIT;
                this.binding.newListTitleTv.setText(R.string.edit_list);
                this.binding.listNameEt.setText(this.shoppingList.getName());
                this.binding.descriptionEt.setText(this.shoppingList.getDescription());
            }
        });

        return binding.getRoot();
    }

    /**
     * Method used to add the created list when clicked on save button.
     * After that it resets the texts in the two textInputs.
     * Finally it shows a toast with the success msg.
     *
     * @param view
     */
    private void handleSaveClick(View view) {
        if (!this.validInputs()) return;

        if (this.mode == CREATE) {
            viewModel.addShoppingList(this.shoppingList).observe(getViewLifecycleOwner(), stringEzResponse -> {
                viewModel.refreshData();
                this.handleSaveSuccess();
            });
            Toast.makeText(this.getContext(), "Lista criada com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateShoppingList(this.shoppingList.getId(), this.shoppingList).observe(this.getViewLifecycleOwner(), response -> {
                viewModel.setCurrentShoppingList(this.shoppingList);
                Toast.makeText(this.getContext(), "Lista editada com sucesso", Toast.LENGTH_SHORT).show();
                this.handleSaveSuccess();
            });
        }
    }

    private boolean validInputs() {
        boolean error = false;
        final String listName = binding.listNameEt.getText().toString().trim();
        final String description = binding.descriptionEt.getText().toString().trim();

        if (listName.isEmpty()) {
            binding.listNameEt.setError(getString(R.string.fill_field));
            error = true;
        }

        if (!error) {
            this.shoppingList.setName(listName);
            this.shoppingList.setDescription(description);
            return true;
        }

        return false;
    }

    private void handleSaveSuccess() {
        this.dismiss();
        binding.listNameEt.setText("");
        binding.descriptionEt.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


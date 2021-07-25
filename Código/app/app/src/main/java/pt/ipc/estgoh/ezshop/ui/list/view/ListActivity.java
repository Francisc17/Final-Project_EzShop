package pt.ipc.estgoh.ezshop.ui.list.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ActivityListBinding;
import pt.ipc.estgoh.ezshop.ui.list.adapter.CategorizedItemsAdapter;
import pt.ipc.estgoh.ezshop.ui.list.interfaces.Callback;
import pt.ipc.estgoh.ezshop.ui.main.view.CreateListBottomSheetDialogFragment;

public class ListActivity extends AppCompatActivity implements Callback {
    private ActivityListBinding binding;
    private ShoppingList shoppingList;
    private ShoppingListsViewModel viewModel;
    private CategorizedItemsAdapter categorizedItemsAdapter;
    private SearchBottomSheetDialogFragment searchFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ShoppingListsViewModel.class);

        shoppingList = (ShoppingList) getIntent().getSerializableExtra("list");
        this.viewModel.setCurrentShoppingList(this.shoppingList);
        if (shoppingList == null)
            finish();

        binding.listNameTv.setTransitionName("listTitleTransition");
        binding.listDescriptionTv.setTransitionName("listDescriptionTransition");
        binding.separator.setTransitionName("listSeparatorTransition");
        binding.sharedIv.setTransitionName("listSharedTransition");

        final CreateListBottomSheetDialogFragment editFrag = new CreateListBottomSheetDialogFragment();
        binding.editIb.setOnClickListener(v -> {
            if (!editFrag.isAdded())
                editFrag.show(getSupportFragmentManager(), editFrag.getTag());
        });

        binding.deleteIb.setOnClickListener(v -> {
            final AlertDialog alert = new AlertDialog.Builder(this, R.style.Theme_EzShop_Dialog)
                    .setTitle(R.string.delete_list)
                    .setMessage(R.string.delete_list_warning)
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        // Continue with delete operation
                        this.viewModel.removeShoppingList(this.shoppingList.getId()).observe(this, stringEzResponse -> finish());
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(R.string.no, null)
                    .create();

            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.show();
        });

        final ShareBottomSheetDialogFragment shareFrag = new ShareBottomSheetDialogFragment();
        binding.shareIb.setOnClickListener(v -> shareFrag.show(getSupportFragmentManager(), shareFrag.getTag()));

        searchFrag = SearchBottomSheetDialogFragment.newInstance(shoppingList.getId());
        searchFrag.setCallback(this);
        binding.addItemFab.setOnClickListener(v -> {
            if (!searchFrag.isAdded())
                searchFrag.show(getSupportFragmentManager(), searchFrag.getTag());
        });

        loadData();
    }

    private void loadData() {
        this.viewModel.getCurrentShoppingList().observe(this, this::loadListHeader);
        final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        this.updateShoppingListItems();
                    }
                });

        binding.categorizedItemsRv.setLayoutManager(new LinearLayoutManager(this));
        this.categorizedItemsAdapter = new CategorizedItemsAdapter(this.viewModel, startForResult);
        binding.categorizedItemsRv.setAdapter(categorizedItemsAdapter);
        this.categorizedItemsAdapter.setItemsGroupByCategory(shoppingList.getItemsGroupByCategory());
    }

    private void loadListHeader(final ShoppingList aShoppingList) {
        this.shoppingList = aShoppingList;
        binding.listNameTv.setText(aShoppingList.getName());
        if (aShoppingList.getDescription() != null && !aShoppingList.getDescription().isEmpty()) {
            binding.listDescriptionTv.setVisibility(View.VISIBLE);
            binding.listDescriptionTv.setText(aShoppingList.getDescription());
        } else
            binding.listDescriptionTv.setVisibility(View.GONE);

        if (!aShoppingList.getIsShared())
            binding.sharedIv.setVisibility(View.INVISIBLE);
        else
            binding.sharedIv.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateShoppingListItems() {
        viewModel.getShoppingList(this.shoppingList.getId()).observe(this, listEzResponse -> {
            if (listEzResponse != null && listEzResponse.getData() != null && listEzResponse.getData().getItemsGroupByCategory() != null)
                // Delay to allow close animation when saving
                Executors.newSingleThreadScheduledExecutor().schedule(() -> runOnUiThread(() -> {
                    binding.categorizedItemsRv.setAdapter(categorizedItemsAdapter);
                    this.categorizedItemsAdapter.setItemsGroupByCategory(listEzResponse.getData().getItemsGroupByCategory());
                }), 250, TimeUnit.MILLISECONDS);
        });
    }
}
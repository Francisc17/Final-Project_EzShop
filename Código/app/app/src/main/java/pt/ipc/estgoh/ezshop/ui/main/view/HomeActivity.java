package pt.ipc.estgoh.ezshop.ui.main.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ActivityMainBinding;
import pt.ipc.estgoh.ezshop.ui.main.adapter.ShoppingListsAdapter;


/*
AppCompatActivity:
Base class for activities that wish to use some of the newer platform features on older Android devices.
 */
public class HomeActivity extends AppCompatActivity {

    /*
    View binding replaces findViewById
    Once view binding is enabled in a module, it generates a binding class for each XML layout file present in that module
    */
    private ActivityMainBinding binding;
    private ShoppingListsViewModel viewModel;

    private ShoppingListsAdapter shoppingListsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Instantiates a layout XML file into its corresponding View objects. It is never used directly.
        Instead, use Activity.getLayoutInflater() to retrieve a standard LayoutInflater instance
        */
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        //getRoot returns the outermost View in the layout file associated with the Binding.
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ShoppingListsViewModel.class);

        binding.listsRv.setLayoutManager(new LinearLayoutManager(this));
        shoppingListsAdapter = new ShoppingListsAdapter();
        binding.listsRv.setAdapter(shoppingListsAdapter);

        final CreateListBottomSheetDialogFragment frag = new CreateListBottomSheetDialogFragment();
        binding.createListFab.setOnClickListener(v -> {
            if (!frag.isAdded())
                frag.show(getSupportFragmentManager(), frag.getTag());
        });

        binding.settingsBtn.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getShoppingLists().observe(this, shoppingListEzResponse -> shoppingListsAdapter.setLists(shoppingListEzResponse != null ? shoppingListEzResponse.getData() : null));
    }
}
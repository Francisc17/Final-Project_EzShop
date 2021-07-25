package pt.ipc.estgoh.ezshop.ui.list.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.viewmodel.ItemViewModel;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.SearchFragmentBinding;
import pt.ipc.estgoh.ezshop.ui.imageclassification.view.ClassifierActivity;
import pt.ipc.estgoh.ezshop.ui.item.view.ArticleExtendedOptionsActivity;
import pt.ipc.estgoh.ezshop.ui.list.adapter.SearchResultsAdapter;
import pt.ipc.estgoh.ezshop.ui.list.interfaces.Callback;

public class SearchBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private SearchFragmentBinding binding;
    private ItemViewModel itemViewMode;
    private ShoppingListsViewModel shoppingListsViewModel;
    private Callback callback;

    public static SearchBottomSheetDialogFragment newInstance(final long aListId) {
        SearchBottomSheetDialogFragment myFragment = new SearchBottomSheetDialogFragment();

        Bundle args = new Bundle();
        args.putLong("listId", aListId);
        myFragment.setArguments(args);

        return myFragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        itemViewMode = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        shoppingListsViewModel = new ViewModelProvider(requireActivity()).get(ShoppingListsViewModel.class);

        final long listId = requireArguments().getLong("listId");

        binding.resultsRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        this.callback.updateShoppingListItems();
                        this.dismiss();
                    }
                });
        final SearchResultsAdapter adapter = new SearchResultsAdapter(listId, startForResult);
        binding.resultsRv.setAdapter(adapter);

        itemViewMode.getItemsLiveData().observe(this, adapter::setResults);

        binding.itemNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2)
                    itemViewMode.searchItem(s.toString());
                else itemViewMode.clearItems();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.itemNameEt.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= ((binding.itemNameEt.getRight() - binding.itemNameEt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) - 50)) {

                    final Intent intent = new Intent(getContext(), ClassifierActivity.class);
                    intent.putExtra("listId", listId);
                    startForResult.launch(intent);
                    return true;
                }
            }
            return false;
        });


        this.binding.newProductBtn.setOnClickListener(v -> {
            final Intent intent = new Intent(getContext(), ArticleExtendedOptionsActivity.class);
            intent.putExtra("listId", listId);
            startForResult.launch(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        shoppingListsViewModel = null;
        itemViewMode = null;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}


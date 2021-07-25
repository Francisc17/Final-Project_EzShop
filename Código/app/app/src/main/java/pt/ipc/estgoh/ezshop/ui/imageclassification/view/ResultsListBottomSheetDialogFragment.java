package pt.ipc.estgoh.ezshop.ui.imageclassification.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.databinding.ResultsListFragmentBinding;
import pt.ipc.estgoh.ezshop.ui.imageclassification.adapter.ResultsAdapter;
import pt.ipc.estgoh.ezshop.ui.imageclassification.viewmodel.ResultsViewModel;
import pt.ipc.estgoh.ezshop.ui.item.view.ArticleExtendedOptionsActivity;

public class ResultsListBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private ResultsListFragmentBinding binding;
    private ResultsViewModel viewModel;
    private ActivityResultLauncher<Intent> startForResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResultsListFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ResultsViewModel.class);

        binding.resultsRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        final ResultsAdapter resultsAdapter = new ResultsAdapter(getParentFragmentManager(), startForResult);
        binding.resultsRv.setAdapter(resultsAdapter);
        viewModel.getResults().observe(this, triple -> {
            resultsAdapter.setListId(triple.getFirst());
            resultsAdapter.setImageLabels(triple.getSecond());
            resultsAdapter.setImage(triple.getThird());

            this.binding.notFoundBtn.setOnClickListener(v -> {
                final Intent intent = new Intent(getContext(), ArticleExtendedOptionsActivity.class);
                intent.putExtra("listId", triple.getFirst());
                intent.putExtra("image", triple.getThird());
                startForResult.launch(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        });

        return binding.getRoot();
    }


    public void setStartForResult(final ActivityResultLauncher<Intent> aStartForResult) {
        this.startForResult = aStartForResult;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = requireActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

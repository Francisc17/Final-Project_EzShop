package pt.ipc.estgoh.ezshop.ui.imageclassification.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.label.ImageLabel;

import java.io.File;
import java.util.List;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.Item;
import pt.ipc.estgoh.ezshop.databinding.ItemResultBinding;
import pt.ipc.estgoh.ezshop.ui.item.view.ArticleExtendedOptionsActivity;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private List<ImageLabel> imageLabels;
    private FragmentManager childFragmentManager;
    private long listId;
    private File image;
    private final ActivityResultLauncher<Intent> startForResult;

    public ResultsAdapter(final FragmentManager aChildFragmentManager, final ActivityResultLauncher<Intent> aStartForResult) {
        this.childFragmentManager = aChildFragmentManager;
        this.startForResult = aStartForResult;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemResultBinding binding;

        public ViewHolder(@NonNull ItemResultBinding aBinding) {
            super(aBinding.getRoot());
            this.binding = aBinding;
        }
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ViewHolder holder, int position) {
        final ImageLabel imageLabel = this.imageLabels.get(position);

        holder.binding.resultNameTv.setText(imageLabel.getText());
        //holder.binding.resultConfidenceTv.setText(String.format(Locale.getDefault(), "%d%%", Math.round(imageLabel.getConfidence() * 100)));


        holder.binding.getRoot().setOnClickListener(v -> {
            final Context context = holder.binding.getRoot().getContext();
            final Intent intent = new Intent(context, ArticleExtendedOptionsActivity.class);
            intent.putExtra("listId", this.listId);
            intent.putExtra("item", new Item(imageLabel.getText(), -1));
            intent.putExtra("image", this.image);
            this.startForResult.launch(intent);
            this.getActivity(context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
    }

    public Activity getActivity(Context context) {
        if (context instanceof ContextWrapper)
            if (context instanceof Activity) return (Activity) context;
            else return getActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }

    @Override
    public int getItemCount() {
        return this.imageLabels == null ? 0 : this.imageLabels.size();
    }

    public void setImageLabels(final List<ImageLabel> imageLabels) {
        this.imageLabels = imageLabels;
        this.notifyDataSetChanged();
    }

    public void setListId(final long aListId) {
        this.listId = aListId;
    }


    public void setImage(final File aImage) {
        this.image = aImage;
    }
}

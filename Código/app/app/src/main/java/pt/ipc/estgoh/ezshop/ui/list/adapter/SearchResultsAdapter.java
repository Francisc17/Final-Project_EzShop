package pt.ipc.estgoh.ezshop.ui.list.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.Item;
import pt.ipc.estgoh.ezshop.databinding.ItemResultBinding;
import pt.ipc.estgoh.ezshop.ui.item.view.ArticleExtendedOptionsActivity;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<Item> items;
    private final long listId;
    private final ActivityResultLauncher<Intent> activityResultLauncher;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemResultBinding binding;

        public ViewHolder(@NonNull ItemResultBinding aBinding) {
            super(aBinding.getRoot());
            this.binding = aBinding;
        }
    }

    public SearchResultsAdapter(final long aListId, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.listId = aListId;
        this.activityResultLauncher = activityResultLauncher;
    }

    @NonNull
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.resultNameTv.setText(this.items.get(position).getName());

        holder.binding.getRoot().setOnClickListener(v -> {
            final Intent intent = new Intent(v.getContext(), ArticleExtendedOptionsActivity.class);
            intent.putExtra("listId", this.listId);
            intent.putExtra("item", this.items.get(position));
            this.activityResultLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.items == null ? 0 : this.items.size();
    }

    public void setResults(final EzResponse<List<Item>> aItems) {
        if (aItems != null && aItems.getData() != null)
            this.items = aItems.getData();
        else
            this.items = null;
        this.notifyDataSetChanged();
    }
}

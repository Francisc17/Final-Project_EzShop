package pt.ipc.estgoh.ezshop.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.databinding.ItemResultBinding;

public class SharedListAdapter extends RecyclerView.Adapter<SharedListAdapter.ViewHolder> {

    private List<String> usersList;

    public SharedListAdapter(final List<String> aUsers) {
        this.usersList = aUsers;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.resultNameTv.setText(this.usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

package pt.ipc.estgoh.ezshop.ui.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.databinding.ItemListsBinding;
import pt.ipc.estgoh.ezshop.ui.list.view.ListActivity;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder> {

    private List<ShoppingList> listsList;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemListsBinding binding;

        public ViewHolder(@NonNull ItemListsBinding aBinding) {
            super(aBinding.getRoot());
            this.binding = aBinding;
        }
    }

    public void setLists(final List<ShoppingList> aListsList) {
        this.listsList = aListsList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemListsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListsAdapter.ViewHolder holder, int position) {
        ShoppingList list = this.listsList.get(position);
        holder.binding.listNameTv.setText(list.getName());
        holder.binding.listDescriptionTv.setText(list.getDescription());
        holder.binding.itemCounterTv.setText(String.format(Locale.getDefault(), "%d/%d", list.getItemsChecked(), list.getItemCounter()));
        if (!list.getIsShared())
            holder.binding.sharedIv.setVisibility(View.GONE);
        else
            holder.binding.sharedIv.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            final Context context = holder.binding.getRoot().getContext();

            holder.binding.listNameTv.setTransitionName("listTitleTransition");
            holder.binding.listDescriptionTv.setTransitionName("listDescriptionTransition");
            holder.binding.separator.setTransitionName("listSeparatorTransition");
            final Pair<View, String> pair1 = Pair.create(holder.binding.listNameTv, holder.binding.listNameTv.getTransitionName());
            final Pair<View, String> pair2 = Pair.create(holder.binding.listDescriptionTv, holder.binding.listDescriptionTv.getTransitionName());
            final Pair<View, String> pair3 = Pair.create(holder.binding.separator, holder.binding.separator.getTransitionName());

            ActivityOptionsCompat optionsCompat;
            if (list.getIsShared()) {
                holder.binding.sharedIv.setTransitionName("listSharedTransition");
                final Pair<View, String> pair4 = Pair.create(holder.binding.sharedIv, holder.binding.sharedIv.getTransitionName());
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, pair1, pair2, pair3, pair4);
            } else {
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, pair1, pair2, pair3);
            }

            final Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra("list", list);
            context.startActivity(intent, optionsCompat.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return listsList != null ? listsList.size() : 0;
    }
}

package pt.ipc.estgoh.ezshop.ui.item.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.Item;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ItemItemBinding;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private final List<ListItem> listItems;
    private final ShoppingListsViewModel viewModel;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemItemBinding binding;

        public ViewHolder(@NonNull ItemItemBinding aBinding) {
            super(aBinding.getRoot());
            this.binding = aBinding;
        }
    }

    public ItemsAdapter(final List<ListItem> aListItems, final ShoppingListsViewModel aViewModel) {
        this.listItems = aListItems;
        this.viewModel = aViewModel;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        holder.binding.getRoot().setOnClickListener(null);
        final ListItem listItem = this.listItems.get(position);
        final Item item = listItem.getItem();

        holder.binding.itemNameTv.setText(item.getName());
        holder.binding.mealCb.setChecked(listItem.isChecked());

        this.handleChecked(listItem, holder.binding);

        holder.binding.getRoot().setOnClickListener((v) -> {
            final boolean isChecked = !holder.binding.mealCb.isChecked();
            holder.binding.mealCb.setChecked(isChecked);
            moveItems(listItem, holder.getLayoutPosition(), isChecked);
            listItem.setChecked(isChecked);
            this.handleChecked(listItem, holder.binding);

            this.viewModel.updateListItem(listItem.getListId(), listItem);
        });
    }

    private void handleChecked(final ListItem listItem, final ItemItemBinding binding) {
        if (listItem.isChecked()) {
            binding.itemNameTv.setPaintFlags(binding.itemNameTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.itemCl.setAlpha(0.5f);
        } else {
            binding.itemNameTv.setPaintFlags(binding.itemNameTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            binding.itemCl.setAlpha(1f);
        }
    }

    private void moveItems(final ListItem listItem, final int aPosition, final boolean aChecked) {

        final ListItem[] listItemsArray = this.listItems.toArray(new ListItem[0]);

        int i = 0;
        if (aChecked) {
            for (; i < listItemsArray.length; i++)
                if (listItemsArray[i].isChecked()) break;

            System.arraycopy(listItemsArray, aPosition + 1, listItemsArray, aPosition, i - 1 - aPosition);
            listItemsArray[i - 1] = listItem;
            this.arrayToList(listItemsArray);
            notifyItemMoved(aPosition, i - 1);
        } else {
            for (i = listItemsArray.length - 1; i >= 0; i--)
                if (!listItemsArray[i].isChecked()) break;

            System.arraycopy(listItemsArray, i + 1, listItemsArray, i + 2, aPosition - (i + 1));
            listItemsArray[i + 1] = listItem;
            this.arrayToList(listItemsArray);
            notifyItemMoved(aPosition, i + 1);
        }
    }

    private void arrayToList(final ListItem[] listItemsArray) {
        this.listItems.clear();
        this.listItems.addAll(Arrays.asList(listItemsArray));
    }

    @Override
    public int getItemCount() {
        return this.listItems.size();
    }
}

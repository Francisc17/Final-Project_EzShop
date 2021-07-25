package pt.ipc.estgoh.ezshop.ui.list.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.Category;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ItemCategorizedItemsBinding;
import pt.ipc.estgoh.ezshop.ui.item.adapter.ItemsAdapter;
import pt.ipc.estgoh.ezshop.ui.item.view.ArticleExtendedOptionsActivity;

public class CategorizedItemsAdapter extends RecyclerView.Adapter<CategorizedItemsAdapter.ViewHolder> {

    private List<Category> itemsGroupByCategory;
    private final ShoppingListsViewModel viewModel;
    private final ActivityResultLauncher<Intent> startForResult;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategorizedItemsBinding binding;

        public ViewHolder(@NonNull ItemCategorizedItemsBinding aBinding) {
            super(aBinding.getRoot());
            this.binding = aBinding;
        }
    }

    public CategorizedItemsAdapter(final ShoppingListsViewModel aViewModel, final ActivityResultLauncher<Intent> aStartForResult) {
        this.viewModel = aViewModel;
        this.startForResult = aStartForResult;
    }

    @NonNull
    @Override
    public CategorizedItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategorizedItemsAdapter.ViewHolder(ItemCategorizedItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategorizedItemsAdapter.ViewHolder holder, int position) {
        final Category category = itemsGroupByCategory.get(position);
        final List<ListItem> items = category.getCategoryItems() != null ? category.getCategoryItems() : new ArrayList<>();
        Collections.sort(items, (o1, o2) -> Boolean.compare(o1.isChecked(), o2.isChecked()));
        holder.binding.itemCategoryTv.setText(category.getName());

        holder.binding.itemsRv.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext()));
        final ItemsAdapter itemsAdapter = new ItemsAdapter(items, viewModel);
        holder.binding.itemsRv.setAdapter(itemsAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                ListItem listItem;
                final int position = viewHolder.getAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        listItem = items.get(position);
                        items.remove(position);
                        itemsAdapter.notifyItemRemoved(position);
                        viewModel.removeItemFromList(listItem.getListId(), listItem.getId()).observe((LifecycleOwner) holder.binding.getRoot().getContext(), response -> {
                            if (response != null)
                                Snackbar.make(holder.binding.itemsRv, listItem.getItem().getName(), Snackbar.LENGTH_LONG)
                                        .setAction("Desfazer", v -> viewModel.associateItemToList(listItem.getListId(), listItem).observe((LifecycleOwner) holder.binding.getRoot().getContext(), res -> {
                                            if (res != null && res.getData() != null) {
                                                listItem.setId(res.getData().getId());
                                                items.add(position, listItem);
                                                itemsAdapter.notifyItemInserted(position);
                                            }
                                        })).show();
                        });
                        break;
                    case ItemTouchHelper.LEFT:
                        listItem = items.get(position);
                        final Context ctx = holder.binding.getRoot().getContext();
                        final Intent intent = new Intent(ctx, ArticleExtendedOptionsActivity.class);
                        intent.putExtra("listItem", listItem);
                        startForResult.launch(intent);
                        ((Activity) ctx).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        viewHolder.itemView.postDelayed(() -> itemsAdapter.notifyItemChanged(position), 500);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView
                    recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    final View itemView = viewHolder.itemView;

                    final Paint p = new Paint();

                    if (dX > 0) {
                        /* Set your color for positive displacement */
                        p.setColor(0xff5a3034);
                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRoundRect(new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX + 50, (float) itemView.getBottom()), 30, 30, p);
                        //Set the image icon for Left swipe
                        Drawable drawable = ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_icon_trash);
                        drawable.setTint(Color.WHITE);

                        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        final Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);

                        c.drawBitmap(bitmap,
                                (float) ((((float) itemView.getRight() - dX) / 4) * 0.4) + bitmap.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - bitmap.getHeight()) / 2,
                                p);

                    } else {
                        /* Set your color for negative displacement */
                        p.setColor(0xff333d47);

                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRoundRect(new RectF((float) itemView.getRight() - 50 + dX, (float) itemView.getTop(),
                                (float) itemView.getRight() - 5, (float) itemView.getBottom()), 30, 30, p);

                        //Set the image icon for Left swipe
                        final Drawable drawable = ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_baseline_arrow_right_alt_24);
                        drawable.setTint(Color.WHITE);

                        final Bitmap bitmap = Bitmap.createBitmap((int) (drawable.getIntrinsicWidth() * 1.5),
                                (int) (drawable.getIntrinsicHeight() * 1.5), Bitmap.Config.ARGB_8888);
                        final Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);

                        c.drawBitmap(bitmap,
                                (float) (((((itemView.getRight() / 4) * 3) - ((dX / 3) * 0.5))) - bitmap.getWidth()),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - bitmap.getHeight()) / 2,
                                p);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(holder.binding.itemsRv);
    }

    @Override
    public int getItemCount() {
        return itemsGroupByCategory == null ? 0 : itemsGroupByCategory.size();
    }

    public void setItemsGroupByCategory(final List<Category> aItemsGroupByCategory) {
        this.itemsGroupByCategory = aItemsGroupByCategory;
        this.notifyItemMoved(0, this.itemsGroupByCategory.size());
    }
}

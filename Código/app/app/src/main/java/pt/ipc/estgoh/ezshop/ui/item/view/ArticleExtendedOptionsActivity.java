package pt.ipc.estgoh.ezshop.ui.item.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.model.Category;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.Item;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.viewmodel.CategoryViewModel;
import pt.ipc.estgoh.ezshop.data.viewmodel.ItemViewModel;
import pt.ipc.estgoh.ezshop.data.viewmodel.ShoppingListsViewModel;
import pt.ipc.estgoh.ezshop.databinding.ActivityInsertProductBinding;

public class ArticleExtendedOptionsActivity extends AppCompatActivity implements View.OnClickListener, Observer<EzResponse<String>> {

    private final static int EDIT_MODE = 0;
    private final static int CREATE_MODE = 1;
    private final static int ASSOCIATE_MODE = 2;

    private ActivityInsertProductBinding binding;
    private ShoppingListsViewModel shoppingListsViewModel;
    private CategoryViewModel categoryViewModel;
    private ItemViewModel itemViewModel;
    private int mode;
    private long listId;
    private ListItem listItem;
    private Item item = new Item();
    private List<Category> categories;
    private final List<File> imagesToUpload = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsertProductBinding.inflate(getLayoutInflater());
        shoppingListsViewModel = new ViewModelProvider(this).get(ShoppingListsViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        setContentView(binding.getRoot());

        this.listId = getIntent().getLongExtra("listId", -1);
        this.item = (Item) getIntent().getSerializableExtra("item");
        this.imagesToUpload.add((File) getIntent().getSerializableExtra("image"));
        this.listItem = (ListItem) getIntent().getSerializableExtra("listItem");
        if (this.listItem != null)
            this.listId = this.listItem.getListId();

        if (listId == -1)
            finish();

        this.mode = listItem != null ? EDIT_MODE : item == null ? CREATE_MODE : ASSOCIATE_MODE;

        // Init list and item
        if (this.listItem == null)
            this.listItem = new ListItem();
        if (this.item == null)
            this.item = new Item();

        this.modeLogic();

        binding.insertProductQuantity.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_RIGHT = 2;

            getWindow().getDecorView().clearFocus();

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= ((binding.insertProductQuantity.getRight() - binding.insertProductQuantity.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) - 50)) {
                    if (this.parseInt() <= 999)
                        binding.insertProductQuantity.setText(String.format(Locale.getDefault(), "%d", this.parseInt() + 1));
                    return true;
                } else if (event.getRawX() >= binding.insertProductQuantity.getLeft() && event.getRawX() <= (binding.insertProductQuantity.getLeft() + binding.insertProductQuantity.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) + 50) {
                    if (this.parseInt() > 1)
                        binding.insertProductQuantity.setText(String.format(Locale.getDefault(), "%d", this.parseInt() - 1));
                    return true;
                }
            }
            return false;
        });
        this.binding.newProductBtn.setOnClickListener(this);
    }

    private void modeLogic() {
        switch (this.mode) {
            case EDIT_MODE:
                this.binding.insertProductTitle.setText(R.string.edit_product);
                this.binding.insertProductName.setText(this.listItem.getItem().getName());
                this.binding.insertProductInfo.setText(this.listItem.getAdditionalInfo());
                this.binding.insertProductQuantity.setText(String.valueOf(this.listItem.getItemQuantity()));
                this.hideFieldsInNonCreateMode(this.listItem.getItem().getName());
                this.binding.newProductBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.binding.newProductBtn.setText(R.string.update_product);
                break;
            case CREATE_MODE:
                this.handleImages();
                this.categoryViewModel.getCategories().observe(this, listEzResponse -> {
                    if (listEzResponse != null && listEzResponse.getData() != null) {
                        this.categories = listEzResponse.getData();
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, this.categories.stream().map(Category::getName).collect(Collectors.toList()));
                        this.binding.insertProductCategory.setAdapter(arrayAdapter);
                    }
                });
                break;
            case ASSOCIATE_MODE:
                this.hideFieldsInNonCreateMode(this.item.getName());
                break;
        }
    }

    private void hideFieldsInNonCreateMode(final String aName) {
        this.binding.insertProductName.setFocusable(false);
        this.binding.insertProductName.setClickable(false);
        this.binding.insertProductName.setLongClickable(false);
        this.binding.insertProductName.setText(Normalizer.normalize(aName, Normalizer.Form.NFC));
        this.binding.insertProductCategory.setVisibility(View.GONE);
        this.binding.insertProductImageBtn.setVisibility(View.GONE);
    }

    private void handleImages() {
        final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            final File image = (File) result.getData().getSerializableExtra("image");
                            if (image != null) {
                                this.imagesToUpload.add(image);
                                this.insertPlaceholder(image);
                            }
                        }
                    }
                });

        this.binding.insertProductImageBtn.setOnClickListener(v -> startForResult.launch(new Intent(this, CameraActivity.class)));
    }

    private void insertPlaceholder(final File image) {
        final CardView cardView = new CardView(this);
        final ImageView imageView = new ImageView(this);
        cardView.addView(imageView);
        binding.imagesPlaceholderGridlayout.addView(cardView);
        cardView.setRadius(15);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.setMargins(15, 15, 15, 15);
        cardView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardView.getLayoutParams().width = (int) (this.getResources().getDisplayMetrics().density * 85);
        cardView.getLayoutParams().height = (int) (this.getResources().getDisplayMetrics().density * 85);
        imageView.setImageBitmap(this.getResizedBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()), (int) (this.getResources().getDisplayMetrics().density * 85)));
//        imageView.setImageURI(Uri.fromFile(image));
        imageView.requestLayout();
        cardView.requestLayout();
        imageView.setOnClickListener(v -> {
            final AlertDialog alert = new AlertDialog.Builder(this, R.style.Theme_EzShop_Dialog)
                    .setTitle(R.string.remove_image)
                    .setMessage(R.string.remove_image_warning)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        binding.imagesPlaceholderGridlayout.removeView(cardView);
                        this.imagesToUpload.remove(image);
                    })
                    .setNegativeButton(R.string.no, null)
                    .create();

            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.show();
        });
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onClick(View v) {
        if (!this.validCreateModeInputs()) return;

        switch (this.mode) {
            case EDIT_MODE:
                this.shoppingListsViewModel.updateListItem(this.listId, this.listItem).observe(this, this);
                return;
            case CREATE_MODE:
                if (this.categories != null) {
                    this.listItem.setItem(this.item);
                    this.shoppingListsViewModel.addItemToList(this.listId, this.listItem).observe(this, response -> {
                        if (this.imagesToUpload == null || this.imagesToUpload.size() == 0)
                            this.onChanged(null);
                    });
                } else
                    return;
                break;
            case ASSOCIATE_MODE:
                this.listItem.setItem(this.item);
                this.shoppingListsViewModel.associateItemToList(listId, listItem).observe(this, response -> {
                    if (this.imagesToUpload == null || this.imagesToUpload.size() == 0)
                        this.onChanged(null);
                });
                break;
        }

        if (this.imagesToUpload != null && this.imagesToUpload.size() > 0 && this.item.getName() != null)
            for (final File file : this.imagesToUpload)
                uploadImage(file);
        this.onChanged(null);
    }


    private boolean validCreateModeInputs() {
        boolean error = false;
        final int itemQuantity = parseInt();
        final String itemName = this.binding.insertProductName.getText().toString().trim();
        final String additionalInfo = this.binding.insertProductInfo.getText().toString().trim();
        final long categoryId = this.mode == CREATE_MODE ? this.categories.get(this.binding.insertProductCategory.getSelectedItemPosition()).getId() : this.item.getCategory();

        if (itemQuantity <= 0 || itemQuantity > 9999) {
            this.binding.insertProductQuantity.setError(getString(R.string.invalid_quantity));
            error = true;
        }

        if (itemName.isEmpty()) {
            this.binding.insertProductName.setError(getString(R.string.fill_field));
            error = true;
        } else if (itemName.length() < 2 || itemName.length() > 50) {
            this.binding.insertProductName.setError(getString(R.string.between2and50));
            error = true;
        }

        if (!error) {
            this.listItem.setListId(this.listId);
            this.listItem.setItemQuantity(parseInt());
            this.listItem.setAdditionalInfo(additionalInfo);
            this.item.setName(itemName);
            this.item.setCategory(categoryId);
            return true;
        }

        return false;
    }

    private int parseInt() {
        try {
            return Integer.parseInt(binding.insertProductQuantity.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void uploadImage(final File aFile) {
        if (aFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), aFile);
            MultipartBody.Part image = MultipartBody.Part.createFormData("image", aFile.getName(), requestFile);
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), this.item.getName());
            this.itemViewModel.uploadPhoto(image, name);
        }
    }

    @Override
    public void onChanged(EzResponse<String> stringEzResponse) {
        setResult(Activity.RESULT_OK);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

package com.example.ultramarket.ui.adminLayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ultramarket.R;
import com.example.ultramarket.adapters.BrandProdAdapter;
import com.example.ultramarket.adapters.CategoryProdAdapter;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductActivity extends AppCompatActivity  {

    private final static int GET_FROM_GALLERY = 1000;
    private final static int READ_MEDIA_PERMISSION_CODE = 1001;

    private ProgressBar upload_image_progress_product;
    private FloatingActionButton btn_upload_img_product;
    private FloatingActionButton btn_save_product;
    private ImageView product_image;
    private EditText product_name;
    private EditText product_desc;
    private EditText product_price;
    private Spinner spinner_currency;
    private Spinner spinner_unit;
    private EditText product_count;
    private EditText product_percent;

    private Uri selectedImage = null;

    private RecyclerView brand_recycler;
    private BrandProdAdapter brandProdAdapter;
    private RecyclerView category_recycler;
    private CategoryProdAdapter categoryProdAdapter;

    private BrandsManagementViewModel brandsManagementViewModel;
    private CategoriesManagementViewModel categoriesManagementViewModel;

    private StorageReference mStorageReference;

    private DatabaseReference productDbReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        setupViews();
        setupRecyclers();

        setupViewModels();
    }

    private void setupViews() {
        upload_image_progress_product = (ProgressBar) findViewById(R.id.upload_image_progress_product);
        btn_upload_img_product = (FloatingActionButton) findViewById(R.id.btn_upload_img_product);
        btn_save_product = (FloatingActionButton) findViewById(R.id.btn_save_product);
        product_image = (ImageView) findViewById(R.id.product_image);
        product_name = (EditText) findViewById(R.id.product_name);
        product_desc = (EditText) findViewById(R.id.product_desc);
        product_price = (EditText) findViewById(R.id.product_price);
        product_count = (EditText) findViewById(R.id.product_count);
        product_percent = (EditText) findViewById(R.id.product_percent);
        setupSpinners();
    }

    private void setupViewModels() {
        brandsManagementViewModel = new ViewModelProvider(this).get(BrandsManagementViewModel.class);
        brandsManagementViewModel.loadAllBrands();

        brandsManagementViewModel.loadAllBrands().observe(this, (List<Brand> brands) -> {
            brandProdAdapter.setBrands(brands);
        });

        categoriesManagementViewModel = new ViewModelProvider(this).get(CategoriesManagementViewModel.class);
        categoriesManagementViewModel.loadAllCategories();

        categoriesManagementViewModel.loadAllCategories().observe(this, (Observer<List<Category>>) categories -> {
            categoryProdAdapter.setCategories(categories);
        });
    }

    private void setupRecyclers() {
        brand_recycler = (RecyclerView) findViewById(R.id.brand_prod_recycler);
        brand_recycler.setLayoutManager(new LinearLayoutManager(this));
        brand_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ArrayList<Brand> brandList = new ArrayList<>();
/*        brandList.add(new Brand("dsf", "Brand1", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand2", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand3", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand4", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand5", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand6", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand7", "fdgffdgdf"));
        brandList.add(new Brand("dsf", "Brand8", "fdgffdgdf"));

 */
        brandProdAdapter = new BrandProdAdapter(this, brandList);
        brand_recycler.setAdapter(brandProdAdapter);

        category_recycler = (RecyclerView) findViewById(R.id.category_prod_recycler);
        category_recycler.setLayoutManager(new LinearLayoutManager(this));
        brand_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ArrayList<Category> categoryList = new ArrayList<>();
    /*    categoryList.add(new Category("dsf", "Category1", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category2", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category3", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category4", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category5", "fdgffdgdf"));
        categoryList.add(new Category("dsf", "Category6", "fdgffdgdf"));

     */
        categoryProdAdapter = new CategoryProdAdapter(this, categoryList);
        category_recycler.setAdapter(categoryProdAdapter);
    }

    private void setupSpinners() {
        spinner_currency = (Spinner) findViewById(R.id.currency_spinner);
        spinner_unit = (Spinner) findViewById(R.id.unit_spinner);
        //spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_currency.setAdapter(adapter1);
        spinner_unit.setAdapter(adapter2);
    }

    public void uploadFromGallery(View view) {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getImageFromGallery();
        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, R.string.allow_permission
                        , Toast.LENGTH_SHORT).show();
            }
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, READ_MEDIA_PERMISSION_CODE);
        }

    }

    private void getImageFromGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_MEDIA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            product_image.setImageURI(selectedImage);
        }
    }


    public void saveProduct(View view) {

        if(selectedImage != null && !product_name.getText().toString().isEmpty()
                && !product_desc.getText().toString().isEmpty() && !product_price.getText().toString().isEmpty()
                && !product_count.getText().toString().isEmpty() && !product_percent.getText().toString().isEmpty()){

            ScrollView main_scroll_view = (ScrollView) findViewById(R.id.main_scroll_view);
            main_scroll_view.fullScroll(ScrollView.FOCUS_UP);
            openViewsUI(false);
            insertProduct(selectedImage, product_name.getText().toString(), product_desc.getText().toString()
                    , Double.parseDouble(product_price.getText().toString()), Integer.parseInt(product_count.getText().toString())
                    , Integer.parseInt(product_percent.getText().toString()),spinner_currency.getSelectedItem().toString()
                    , spinner_unit.getSelectedItem().toString());

        }else{
            Toast.makeText(this, "Please, enter all data", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertProduct(Uri selectedImage, String name, String description, double price,
                               int count, int percentage, String currency, String unit) {

        String brand_id = brandProdAdapter.getSelected().getID();
        String category_id = categoryProdAdapter.getSelected().getID();

        mStorageReference = FirebaseStorage.getInstance().getReference().child("Products");

        final StorageReference photoRef = mStorageReference.child(selectedImage.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(selectedImage);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return photoRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProductActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();
                Uri downloadUri = task.getResult();
                String imageUrl = downloadUri.toString();
                productDbReference = FirebaseDatabase.getInstance().getReference(Product.class.getSimpleName());
                String id = productDbReference.push().getKey();
                Product product = new Product(id, name, imageUrl, unit, price, currency, count, description
                        ,(percentage>0), percentage, brand_id, category_id, new Date().getTime());

                productDbReference.child(id).setValue(product);
                Toast.makeText(ProductActivity.this, R.string.product_added, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductActivity.this, R.string.failed_upload, Toast.LENGTH_SHORT).show();
            }
            openViewsUI(true);
            finish();
            onBackPressed();
        });
    }

    private void openViewsUI(boolean isInteractive) {
        if (isInteractive)
            upload_image_progress_product.setVisibility(View.GONE);
        else
            upload_image_progress_product.setVisibility(View.VISIBLE);

        btn_upload_img_product.setEnabled(isInteractive);
        btn_save_product.setEnabled(isInteractive);
        product_name.setEnabled(isInteractive);
        product_desc.setEnabled(isInteractive);
        product_price.setEnabled(isInteractive);
        product_count.setEnabled(isInteractive);
        product_percent.setEnabled(isInteractive);
    }

}
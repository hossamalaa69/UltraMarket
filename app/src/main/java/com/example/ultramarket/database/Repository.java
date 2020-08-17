package com.example.ultramarket.database;

import androidx.lifecycle.LiveData;

import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.database.Entities.Product;

import java.util.ArrayList;

public interface Repository {

    // user categories DAO
    void insertCategory(Category category);

    void insertCategoryList(ArrayList<Category> categories);

    void updateCategory(Category category);

    void updateCategoryById(String id);

    LiveData<ArrayList<Category>> loadAllCategories();

    LiveData<Category> loadCategoryById(String id);

    void deleteCategory(Category category);

    void deleteCategoryById(String id);

    //----------------------------------------------------------------------------------------------
    // user categories DAO
    void insertProduct(Product product);

    void insertProductList(ArrayList<Product> products);

    void updateProduct(Product product);

    void updateProductById(String id);

    LiveData<ArrayList<Product>> loadAllProducts();

    LiveData<Product> loadProductById(String id);

    void deleteProduct(Product product);

    void deleteProductById(String id);

    //----------------------------------------------------------------------------------------------
    // user brands DAO
    void insertBrand(Brand brand);

    void insertBrandList(ArrayList<Brand> brands);

    void updateBrand(Brand brand);

    void updateBrandById(String id);

    LiveData<ArrayList<Brand>> loadAllBrands();

    LiveData<Brand> loadBrandById(String id);

    void deleteBrand(Brand brand);

    void deleteBrandById(String id);
    //----------------------------------------------------------------------------------------------
}

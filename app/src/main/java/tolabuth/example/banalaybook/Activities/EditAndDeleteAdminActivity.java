package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import tolabuth.example.banalaybook.databinding.ActivityEditAndDeleteAdminBinding;

public class EditAndDeleteAdminActivity extends AppCompatActivity {
    private ActivityEditAndDeleteAdminBinding binding;
    private String bookId;
    private ProgressDialog progressDialog;
    //create arraylist type String for to get category from
    private ArrayList<String>categoryTitleArrayList, categoryIdArrayList;
    //create TAG for to check progress of executed
    private final static String TAG = "Book_Edit_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAndDeleteAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get book Id  from intent of Adapter Admin Pdf
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        //setup progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //Click Back , previous activity
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //load category title from Table Category in information Database
        loadCategory();
        loadBookInfo();
        //handle click , pick category
        binding.edtBookCategoryPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });
        //handle click , begin upload
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();

            }
        });
    }
    private String title, description;
    private void ValidateData() {
        //get data form view
        title = binding.titlePdf.getText().toString().trim();
        description = binding.edtDescriptionPdf.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter title book....",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter description...",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedCategoryId)){
            Toast.makeText(this, "Please pick category....",Toast.LENGTH_SHORT).show();
        }else {
            updatePdf();
        }
    }

    private void updatePdf() {
        Log.d(TAG, "UpdatePDF: starting update pdf information to db...");
        //show progress action
        progressDialog.setMessage("Updating pdf information to ....");
        progressDialog.show();
        //setup data to db
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("title", title);
        hashMap.put("description",description);
        hashMap.put("categoryId",selectedCategoryId);
        //start updating
        DatabaseReference refUpdate = FirebaseDatabase.getInstance().getReference("Books");
        refUpdate.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Book Updated.....");
                        progressDialog.dismiss();
                        Toast.makeText(EditAndDeleteAdminActivity.this,"Book Updated Successfully..",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(EditAndDeleteAdminActivity.this, "Updating book information faired..",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadBookInfo() {
        Log.d(TAG, "LoadBookInfo: loading book information from firebasedatabase");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book Information
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String title = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        //setup to views
                        binding.titlePdf.setText(title);
                        binding.edtDescriptionPdf.setText(description);
                        Log.d(TAG, "onDataChange: Loading Book Category Info");
                        //Loading book for to set up to EditText category
                        DatabaseReference refCategoryBook = FirebaseDatabase.getInstance().getReference("Categories");
                        refCategoryBook.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                  //get category
                                  String categoryText = ""+snapshot.child("category").getValue();
                                  //set to category text view
                                   binding.edtBookCategoryPdf.setText(categoryText);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Declare selectedCategoryId, selectedCategoryTitle;
    private String selectedCategoryId, selectedCategoryTitle;
//category dialog
    private void categoryDialog(){
        //make string array from arraylist of String
        String[] categoryArray = new String[categoryTitleArrayList.size()];
        for (int i =0; i<categoryTitleArrayList.size();i++){
            categoryArray[i] = categoryTitleArrayList.get(i);
        }
        //alert Dialog of category
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoryArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCategoryId = categoryIdArrayList.get(i);
                        selectedCategoryTitle = categoryTitleArrayList.get(i);
                        binding.edtBookCategoryPdf.setText(selectedCategoryTitle);
                    }
                }).show();
    }
    private void loadCategory() {
        Log.d(TAG, "LoadCategores: Loading category.......");
        //initial arraycategoryTitle,ID
        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    //add id and category to arraylist (id and title)
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                    Log.d(TAG, "onDataChange: ID: "+id);
                    Log.d(TAG, "onDataChange: Category: "+category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
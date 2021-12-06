package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import tolabuth.example.banalaybook.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity {
   private ActivityCategoryBinding binding;
   //initial
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initail
        firebaseAuth = FirebaseAuth.getInstance();
        //configure progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");

        progressDialog.setCanceledOnTouchOutside(false);
        binding.btnCategorySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });

        binding.imggeCategoryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CategoryActivity.this, DashboardAdminActivity.class));
                finish();
            }
        });

    }
    private String category = "";
    private void validatedata() {
        //before adding     valide category;
        //get data
        category = binding.edtCategoryName.getText().toString().trim();
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please enter category...", Toast.LENGTH_SHORT).show();
        }else {
            addCategoryFirebase();
        }

    }

    private void addCategoryFirebase() {
        //show progress
        progressDialog.setMessage("Adding Category....");
        progressDialog.show();
        //get timestemp
        long timestemp = System.currentTimeMillis();
        //setup info add in firebase
        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("id", ""+timestemp);
        hashMap.put("category", ""+category);
        hashMap.put("timestemp", timestemp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        //add category to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestemp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this,"Category add successfully..", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
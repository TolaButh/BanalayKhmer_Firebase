package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterCategory;

import tolabuth.example.banalaybook.R;
import tolabuth.example.banalaybook.databinding.ActivityDashboardAdminBinding;
import tolabuth.example.banalaybook.model.ModelCategory;

public class DashboardAdminActivity extends AppCompatActivity {
    //view binding
    private ActivityDashboardAdminBinding binding;
    //firebase author
    private FirebaseAuth firebaseAuth;
    private ImageButton imBLogout;
    private TextView tvDAminEmail;
    private Button btnAddCategory;
    private FloatingActionButton flbAddPDF;
    private ArrayList<ModelCategory>modelCategories;
    private AdapterCategory adapterCategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initial Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        imBLogout = findViewById(R.id.Imb_dAdminlogout);
        tvDAminEmail = findViewById(R.id.tv_dAdmin_Email);
        btnAddCategory = findViewById(R.id.btn_add_category);
        flbAddPDF = findViewById(R.id.flb_add_pdf);

        checkUser();
        loadCategory();
        //search data from database
        binding.edtSearchAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                adapterCategory.getFilter().filter(charSequence);
            }catch (Exception e){

            }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //handler logout
        imBLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        //handler click add category
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, CategoryActivity.class));
                finish();
            }
        });
        //flaot action But
        flbAddPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        startActivity(new Intent(DashboardAdminActivity.this, PdfAddActivity.class));
        finish();
            }
        });
        //handle click , open of Profile
        binding.btnProfileAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardAdminActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


    }

    private void loadCategory() {
        //initial
        modelCategories = new ArrayList<>();

        //Databasereference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before add data to it
                modelCategories.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    //get data
                    ModelCategory datamodel = ds.getValue(ModelCategory.class);
                    //add   to arraylist
                    modelCategories.add(datamodel);

                }
                //setup data
                adapterCategory = new AdapterCategory(DashboardAdminActivity.this, modelCategories);
                //set data to recycleview
                binding.recycleAdmin.setLayoutManager(new LinearLayoutManager(DashboardAdminActivity.this));
                binding.recycleAdmin.setAdapter(adapterCategory);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        //getCurrent User
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser== null){
            //not logged in , goto main screen
            startActivity(new Intent(DashboardAdminActivity.this, MainActivity.class));
            finish();

        }else {
            //logged in,get user info
            String email = firebaseUser.getEmail();
            tvDAminEmail.setText(email );
        }
    }
}
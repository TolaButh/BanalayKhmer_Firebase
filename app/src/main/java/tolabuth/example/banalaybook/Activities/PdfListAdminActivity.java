package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterAdminPdf;

import tolabuth.example.banalaybook.databinding.ActivityPdfListAdminBinding;
import tolabuth.example.banalaybook.model.ModelPdf;

public class PdfListAdminActivity extends AppCompatActivity  {
    private ActivityPdfListAdminBinding binding;
    //arraylist to holder   list data of type model pdf
    private ArrayList<ModelPdf>pdfArrayList;
    private AdapterAdminPdf adapterAdminPdf;
    private String categoryId, categoryTitle;
    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get data from intent
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("category");
        //set catogory pdf
        binding.tvPdfSubtilte.setText(categoryTitle);
        loadPdfList();
        //search book
        binding.edtSearchPdf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search as and when
                try {
                    adapterAdminPdf.getFilter().filter(charSequence);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //handle click , go to previous activity;
        binding.IBPDFBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadPdfList() {
        //initial arrayPDFList before adding data
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "LoadPDFList Success");
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            //add to list
                            Log.d(TAG,"Title"+model.getTimestamp());

                            pdfArrayList.add(model);
                            Log.d(TAG,"onDataChange: "+model.getId()+""+model.getTitle());
                        }
                        //setup adapter
                        adapterAdminPdf = new AdapterAdminPdf(PdfListAdminActivity.this,pdfArrayList);
                        binding.rcvBook.setLayoutManager(new LinearLayoutManager(PdfListAdminActivity.this));
                        binding.rcvBook.setAdapter(adapterAdminPdf);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG,""+error.getMessage() );
                        Toast.makeText(PdfListAdminActivity.this,"Load Pdf List Error",Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
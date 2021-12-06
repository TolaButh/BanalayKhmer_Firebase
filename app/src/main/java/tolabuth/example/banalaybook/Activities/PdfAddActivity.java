package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import tolabuth.example.banalaybook.databinding.ActivityPdfAddBinding;

public class PdfAddActivity extends AppCompatActivity {
    //view bing
    private ActivityPdfAddBinding binding;
    //firebase author
    private FirebaseAuth firebaseAuth;
    //Tag for debugging
    private final static String TAG="ADD_FOR_PDF";
    private final static int PDF_PICK_CODE=1000;
    //Uri
    private Uri uriPdf;
    //ArrayList to holder pdf category
    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;
    //initail aler dialog
    private ProgressDialog dialog;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initial firebaseauthor
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategory();
        //setup progress dialog
        dialog = new ProgressDialog(PdfAddActivity.this);
        dialog.setTitle("Please wait");
        dialog.setCanceledOnTouchOutside(false);



        //come back to dashboardAdmin
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PdfAddActivity.this, DashboardAdminActivity.class));
            }
        });
        //handler click go attach
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();

            }
        });
        //handler click category
        binding.typeBookCategoryPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });
        //handler click uploadijng
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //valide data
                validateData();
            }
        });
    }
    String title, description, category;
    private void validateData() {

        //step 1: validate data
        title = binding.titlePdf.getText().toString().trim();
        description = binding.descriptionPdf.getText().toString().trim();
       // category = binding.typeBookCategoryPdf.toString().trim();
        //validete date
        if (TextUtils.isEmpty(title)){
            Toast.makeText(PdfAddActivity.this,"Enter title...",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(PdfAddActivity.this, "Enter description",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(PdfAddActivity.this, "Category Pick...",Toast.LENGTH_SHORT).show();

        }
        else if (uriPdf == null){
            Toast.makeText(PdfAddActivity.this, "Pick PDF....",Toast.LENGTH_SHORT).show();
        }else {
            //all data is validate we can upload now
            uploadPdfToStorage();

        }

    }

    private void uploadPdfToStorage() {
        //step2: upload pdf
        Log.d(TAG,"uploadPDFStrorage: uploading now");
        //show dialog
        dialog.setMessage("Uploading PDF to storage....");
        dialog.show();
        //time stemp
        long timestamp = System.currentTimeMillis();
        //path pdf in firebase Stronge
        String filePathAddName = "Books/"+timestamp;
        //Storage firebase
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAddName);


        storageReference.putFile(uriPdf)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSucccess: PDF uploaded to storage..");
                        Log.d(TAG,"onSuccess: getting odf url");
                        //get pdf url
                        Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while ((!uriTask.isComplete()));
                        Uri uri =uriTask.getResult();
                        //String uploadPDF = ""+uriTask.getResult();
                        //upload to firebase db
                        String uploadPDF = uri+"";
                        uploadPDFInfor(uploadPDF, timestamp);
                        dialog.dismiss();
                        Toast.makeText(PdfAddActivity.this,"Upload pdf successfully",Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure (@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(PdfAddActivity.this,"PDF Upload to storage"+e.getMessage(),Toast.LENGTH_SHORT).show();
               // uploadPDFInfor(uriPdf+"", timestamp);
            }
        });




    }

    private void uploadPDFInfor(String uploadPDF, long timestamp) {
        //step2: upload pdf to firebase
        Log.d(TAG,"uploadPDFFirebase: uploading pdf infor to firebase database...");
        dialog.setMessage("Uploading pdf info....");
        //get id from firebase auth
        String id = firebaseAuth.getUid();
        //setup data to upload

        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("uid", ""+id);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("categoryId", ""+selectedCategoryId);
        hashMap.put("url", ""+uploadPDF);
        hashMap.put("timestemp",""+timestamp);
        hashMap.put("viewsCount",0);
        hashMap.put("downloadsCount",0);
        //DB reference DB=>Book
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfAddActivity.this, "Upload PDF Successfull",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "OnFailue: Faile to upload to db due to "+e.getMessage());
                    dialog.dismiss();
                    Toast.makeText(PdfAddActivity.this, "Failue to upload to db due to ",Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void loadPdfCategory() {
        Log.d(TAG, "LoadPdfCategory: Loading pdf category");
        //initial
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();
        //db reference to load category
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    //get id and title for category

                    String categoryId = ds.child("id").getValue()+"";
                    String categoryTitle = ds.child("category").getValue()+"";
//add to respective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//select id anc title
    private     String selectedCategoryId, selectedCategoryTitle;
    private void categoryPickDialog() {
        //first we go to category
        Log.d(TAG, "CategoryPickDialog: showing category picking dialog");
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);

        }
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PdfAddActivity.this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //handle item click
                        //get click item from list
                        selectedCategoryId =categoryIdArrayList.get(i);
                        selectedCategoryTitle = categoryTitleArrayList.get(i);
                        binding.typeBookCategoryPdf.setText(selectedCategoryTitle);
                        Log.d(TAG,"onClick: Select Category: "+selectedCategoryTitle);
                    }
                }).show();

    }


    private void pdfPickIntent() {
        Log.d(TAG,"pdfPickIntent: Starting pdf pick intent");
        Intent  intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"),PDF_PICK_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK){
            if (requestCode == PDF_PICK_CODE) {
                Log.d(TAG, "onActivityResult: PDF PICK");
                uriPdf = data.getData();
                Log.d(TAG, "onActivityResult: URL:" + uriPdf);
            }

        }else {
            Log.d(TAG , "onActivityResult: Cancel picking Pdf");
            Toast.makeText(PdfAddActivity.this, "Cancel Picking PDF",Toast.LENGTH_SHORT).show();


        }
    }
}
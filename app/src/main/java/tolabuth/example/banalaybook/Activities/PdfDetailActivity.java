package tolabuth.example.banalaybook.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tolabuth.example.banalaybook.MyApplication;
import tolabuth.example.banalaybook.R;
import tolabuth.example.banalaybook.databinding.ActivityPdfDetailBinding;

public class PdfDetailActivity extends AppCompatActivity {
    //view bing

    private ActivityPdfDetailBinding binding;
    //pdf id get from intent
    String bookId, bookTitle, bookUrl;
    //boolean of
    boolean isInFavorite = false;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get data from intent e.g bookid
        bookId = getIntent().getStringExtra("bookId");
        //at start
        binding.btnDownloadView.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }
        loadBookDetail();
        //increment book view count, whenever this page start
        MyApplication.incrementBookViewCount(bookId);
        //handle click book
        binding.IBDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //handle click book , for to open read page
        binding.btnReadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PdfDetailActivity.this, ReadBookPdf.class);
                intent.putExtra("bookId",bookId);
                startActivity(intent);
            }
        });
        //handle click download , for to download to create folder
        binding.btnDownloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //checking permission
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    MyApplication.downloadBook(PdfDetailActivity.this, ""+bookId, ""+bookTitle, ""+bookUrl);
                }else {
                    resultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                }
            }
        });
        //request storage permision
        binding.btnFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(PdfDetailActivity.this, "You's not logged in ",Toast.LENGTH_SHORT).show();

                }else {
                    if (isInFavorite){
                        //in favorite ,remove from favorite
                        MyApplication.removeFromFavorite(PdfDetailActivity.this, bookId);
                    }else {
                        //not in favorite ,add to favorite
                        MyApplication.addToFavorite(PdfDetailActivity.this, bookId);
                    }
                }
            }
        });

    }
//
//    private ActivityResultLauncher<String> resultLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
//            if (isGranted){
//               MyApplication.downloadBook(this, ""+bookId,""+bookTitle,""+bookUrl);
//            }else {
//                //Toast.makeText(this, "Permission was denied...",Toast.LENGTH_SHORT).show();
//                //MyApplication.downloadBook(this, ""+bookId,""+bookTitle,""+bookUrl);
//            }
//    });
private ActivityResultLauncher<String> resultLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                MyApplication.downloadBook(this, ""+bookId,""+bookTitle,""+bookUrl);
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
               // Toast.makeText(this, "Permission was denied...",Toast.LENGTH_SHORT).show();
            }
        });
//load Book Detail
    private void loadBookDetail() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String category = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        bookUrl  = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("id").getValue();

                        //required data is loaded, show data
                        binding.btnDownloadView.setVisibility(View.VISIBLE);
                        //formate date
                       String date = MyApplication.formatTimeStamp(Long.parseLong(timestamp));
                        //load category
                        MyApplication.loadCategory(
                                ""+category
                                ,binding.tvDetailCategory
                        );
                        //load pdf from url
                        MyApplication.loadPdfFromUrl(
                                ""+bookUrl
                                ,""+bookTitle
                                ,binding.pdfDetailView
                                ,binding.progressbarDetail
                                ,binding.tvDetailPage
                        );
                        //load size of pdf
                        MyApplication.loadPdfSize(
                                ""+bookUrl
                                ,""+bookTitle
                                ,binding.tvDetailSize
                        );
//                        //load page count
//                        MyApplication.loadPdfPageCount(
//                                PdfDetailActivity.this
//                                ,""+ bookUrl
//                                , binding.tvDetailPage
//                        );
                        //set data to pdfdetail activity
                        binding.tvDetailTitle.setText(bookTitle);
                        binding.tvDescriptionDetail.setText(description);
                        binding.tvDetailView.setText(viewsCount);
                        binding.tvDetailDownload.setText(downloadsCount);
                        binding.tvDetailDate.setText(date);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void  checkIsFavorite() {
        //logged in check if its in favorite is list or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInFavorite = snapshot.exists();//true : if exists, false if not exist
                        if (isInFavorite) {
                            //exist is favorite
                            binding.btnFavoriteView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_baseline_favorite_24, 0, 0);
                            binding.btnFavoriteView.setText("Remove Favorites");
                        } else {
                            //not exist in favorite
                            binding.btnFavoriteView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_border_24,0,0);
                            //resultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            binding.btnFavoriteView.setText("Add Favorites");
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    //handle click ,add/remove favorites


}
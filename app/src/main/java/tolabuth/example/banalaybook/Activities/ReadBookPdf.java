package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import tolabuth.example.banalaybook.databinding.ActivityReadBookPdfBinding;
import tolabuth.example.banalaybook.Constants;

public class ReadBookPdf extends AppCompatActivity {
    private ActivityReadBookPdfBinding binding;

    private String bookId ;
    //tag for to check
    private  final  static  String TAG = "TAG_VIEW_PDF";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadBookPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get bookId from intent of Detail page
        bookId= getIntent().getStringExtra("bookId");
        //handle click , go back
        binding.IBDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        loadBookForToRead();


    }

    private void loadBookForToRead() {
        //Database reference to go detail
        //Step 1) Get Book Url using    bookId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book url
                        String bookUrl = ""+snapshot.child("url").getValue();
                        //step 2) load pdf  using   that url from firebase storage
                        loadBookFromUrl(bookUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBookFromUrl(String bookUrl) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        binding.progressBar.setVisibility(View.GONE);
                        //load pdf using bytes
                        binding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)//set false to scroll  verticall , set true to swipe horizotal
                            .onPageChange(new OnPageChangeListener() {
                                @Override
                                public void onPageChanged(int page, int pageCount) {
                                    //set current   and total pages in toolbar subtitle
                                    int currentPage = (page+1);//do +1 because page     starts froms
                                    binding.tvDetailTitle.setText(currentPage+"/"+pageCount);

                                }
                            })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(ReadBookPdf.this, "OnError : "+t.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                }).onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                Toast.makeText(ReadBookPdf.this, "OnPageError : "+t.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        }).load();
                        binding.progressBar.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    //Failed to load book
                        binding.progressBar.setVisibility(View.GONE);

                    }
                });

    }
}
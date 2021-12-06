package tolabuth.example.banalaybook;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApplication extends Application {
    private final static String TAG = "Book_Download_TAG";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    //application class runs before your launch
    public final static  String formatTimeStamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format time stamp to dd/mm/yy
        String date = DateFormat.format("dd/MM/yyyy",cal).toString();
        return date;


    }
    //method delete Book from Storage and information from firebasedatabase
    public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {
        String TAG = "DELETE_BOOK_TAG";

        //initial progressDialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
      //setup progressDialog
        Log.d(TAG, "deleteBook: delete....");
        progressDialog.setMessage("Deleting "+bookTitle);
        progressDialog.show();
        Log.d(TAG,"deleteBook: Deleting from storage....");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Delete From Storage");
                        Log.d(TAG, "onSuccess: Delete book information in Database");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Remove Value success
                                        Log.d(TAG, "onSuccess: delete book information successfully!!!");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Book delete successfully!!!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.d(TAG, "onFailure: Delete information in Database");
                                        Toast.makeText(context, "Failure delete information in database"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DeleteBook: delete book from storage failure");
                        Toast.makeText(context, "Failure delete book from storage!!!",Toast.LENGTH_SHORT).show();

                    }
                });


    }
    //load pdf size
    public static void loadPdfSize(String pdfUrl, String pdfTtle, TextView size) {
        String TAG ="LOAD_PDF_SIZE";
        //using url we can get file and its metadata from firebase storage

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size in bytes
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+pdfTtle + ""+bytes);

                        //convert bytes to KB, MB
                        double kb = bytes/1024;
                        double mb = kb/1024;
                        if (mb>1){
                            size.setText(String.format("%.2f", mb)+" MB");
                        }else if (kb>1){
                            size.setText(String.format("%.2f", kb)+" KB");

                        }else {
                            size.setText(String.format("%.2f", bytes)+" Bytes");

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed getting metadata
                Log.d(TAG, "onFaired: "+e.getMessage());

            }
        });
    }
    //load FormUrl
    public static void loadPdfFromUrl(String pdfUrl, String pdfTitle, PDFView pdfView, ProgressBar progressBar,TextView pageTv) {
        //using     url we can get file and         its     metadata
        String TAG = "LoadPDF_FROMURL";
         long MAX_BYTES_PDF =50000000 ;
        //String pdfUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //setup to pdfview
                        pdfView.fromBytes(bytes)
                                .pages(0)//show only first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        //hinde Error
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());


                                    }
                                }).onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                Log.d(TAG, "onPageError: "+t.getMessage());

                            }
                        }).onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                //hinde progressbar
                                progressBar.setVisibility(View.INVISIBLE);
                                //if    page param  is not
                                if (pageTv != null){
                                    pageTv.setText(""+nbPages);
                                }

                            }
                        }).load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());


                    }
                });


    }
    //load Category
    public static void loadCategory(String categoryId,TextView categorys) {
        //get category using categoryId
        //String categoryId = model.getCategoryId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category from table categories of firebase
                        String category = ""+snapshot.child("category").getValue();
                        //set category to text view
                        categorys.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {



                    }
                });


    }
    //load increment book view count
    public static  void incrementBookViewCount(String bookId){
        //1). get book views for count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get view count
                         String viewCount = ""+snapshot.child("viewsCount").getValue();
                         //in case of null replace with 0
                        if(viewCount.equals("") || viewCount.equals(null)){

                            viewCount="0";
                        }
                        //increment view Count
                        long newViews = Long.parseLong(viewCount)+1;
                        //set update viewcount
                        HashMap<String, Object>hashMap = new HashMap<>();
                        hashMap.put("viewsCount",newViews);
                        //update view go to database
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void downloadBook(Context context, String bookId,String bookTitle, String bookUrl){

        Log.d(TAG, "DownloadBook: Download Booking....");
        //set name with extension of book
        String nameWithExtention = bookTitle + ".pdf";
        //progress Dialog of book
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Please waiting");
        dialog.setMessage("Downloading "+nameWithExtention+"...");//e.g.
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //download from firebase storage    using   url
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                    Log.d(TAG, "onSuccess: Book Downloaded");
                    Log.d(TAG, "onSuccess: Saving book....");
                    saveDownloadedBook(context, dialog, bytes,nameWithExtention, bookId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Faired to download due to "+e.getMessage());
                        Toast.makeText(context, "Failured to download due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

    }

    public static void saveDownloadedBook(Context context, ProgressDialog dialog, byte[] bytes, String nameWithExtention, String bookId) {
        try{
            Log.d(TAG, "SaveDownloadedBook: Saving download book");
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdirs();
            String filePath = downloadsFolder.getPath() + "/"+nameWithExtention;
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(bytes);
            outputStream.close();
            Toast.makeText(context, "Saved to Download Folder ",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "saveDownloadedBook: Saved to Download Folder");
            dialog.dismiss();
            incrementBookDownloadCount(bookId);

        }catch (Exception e){
            Log.d(TAG, "saveDownloadedBook: Failured to save download book");
            dialog.dismiss();

        }
    }
    //load  pdf Page for to Count
    public static  void  loadPdfPageCount(Context context, String pdfUrl, TextView tvPage){
        //load  pdf from firebase storage using     url
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //file received

                        //load pdf pages using  pdfView Library
                        PDFView pdfView = new PDFView(context, null);
                        pdfView.fromBytes(bytes)
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        //pdf loaded from bytes we got from firebase storage ,we can now show number of pages

                                        Log.d(TAG, "loadComplete: Load page success ");
                                        tvPage.setText(""+nbPages);

                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailureLoadPdfPageCount: "+e.getMessage());
                        Toast.makeText(context, "This is "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public static void incrementBookDownloadCount(String bookId) {
        Log.d(TAG,"incrementBookDownloadCount: Increment Book Download Count");
        //step1: Get previous download count

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadCount = ""+snapshot.child("downloadsCount").getValue();
                        Log.d(TAG,"onDataChange: Downloads Count: "+downloadCount );
                        if (downloadCount.equals("") || downloadCount.equals(null)){
                            downloadCount = "0";
                        }
                        //converts to long and increment 1
                         long newDownloadsCount = Long.parseLong(downloadCount)+1;
                        //setup data for to update
                         HashMap<String, Object>hashMap = new HashMap<>();
                         hashMap.put("downloadsCount",newDownloadsCount);
                         //steps 2) Updates     new incremented download count to do
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(bookId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Downloads Count updates.....");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Downloads count updates is Failure");


                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    //load add favorite
    public static void  addToFavorite(Context context, String bookId){
        //we can add only if user is logged in
        //10)check if user logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            //not logged in ,can't add favorites
            Toast.makeText(context, "You're not logged In", Toast.LENGTH_SHORT).show();
        }
        else {
                long timestamp = System.currentTimeMillis();
                //setup data to in firebase db of current user for favorite
            HashMap<String, Object>hashMap = new HashMap<>();
            hashMap.put("bookId", bookId);
            hashMap.put("timestamp", timestamp);
            //save to database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Add to favorite list ...",Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, "Failed to add to favorite list ...",Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
    //remove favorite
    public static void removeFromFavorite(Context context, String bookId){
        //we can add only if user is logged in
        //10)check if user logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            //not logged in ,can't add favorites
            Toast.makeText(context, "You're not logged In", Toast.LENGTH_SHORT).show();
        }
        else {


            //save to database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Remove  to favorite list ...",Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, "Remove failed to add to favorite list ...",Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
    //check Is favorite


}

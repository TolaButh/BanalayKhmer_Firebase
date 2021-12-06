package tolabuth.example.banalaybook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterPdfView;
import tolabuth.example.banalaybook.databinding.FragmentViewPagerAdapterBinding;
import tolabuth.example.banalaybook.model.ModelPdf;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksUserFragment extends Fragment {
    private String categoryId;
    private String category;
    private String uid;
    private ArrayList<ModelPdf>pdfArrayList;
    //view binding
    private FragmentViewPagerAdapterBinding binding;
    private AdapterPdfView adapterPdfUser;

    private final static String TAG = "Book_User_TAG";



    public BooksUserFragment() {
        // Required empty public constructor
    }


    public static BooksUserFragment newInstance(String id, String category,String uid) {
        BooksUserFragment fragment = new BooksUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId",id);
        args.putString("category",category);
        args.putString("uid",uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding   = FragmentViewPagerAdapterBinding.inflate(LayoutInflater.from(getContext()),container,false);

        if (category.equals("All")) {
            //load all books
            loadAllBooks();

        }else if (category.equals("Most View")) {
            loadMostViewedDownloadBooks("viewsCount");
        //load most viewed books
            loadMostViewedDownloadBooks("viewsCount");

        }else if (category.equals("Most Download")){
            //load most downloads books
            loadMostViewedDownloadBooks("downloadsCount");
        }else {
        //load selected category books
          loadCategorizedBook();
        }
        //search
        binding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //called as and when ùser type any letter
                try {
                    adapterPdfUser.getFilter().filter(charSequence);
                }catch (Exception e){
                    Log.d(TAG,"onTextChangeed: "+e.getMessage());

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return binding.getRoot();
    }

    private void loadCategorizedBook() {
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild("categoryId").equalTo(categoryId) //load most viewed or download books
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear before add data to arraylist
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            //add to list
                            pdfArrayList.add(model);

                        }
                        //setup adapter
                        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
                        binding.booksRv.setLayoutManager(layoutManager);

                        adapterPdfUser = new AdapterPdfView(getContext(), pdfArrayList);
                        //setup adapter to recycleview
                        binding.booksRv.setAdapter(adapterPdfUser);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadMostViewedDownloadBooks(String orderBy) {
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild(orderBy).limitToLast(10) //load most viewed or download books
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before add data to arraylist
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    //add to list
                    pdfArrayList.add(model);

                }
                //Gride
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
                binding.booksRv.setLayoutManager(layoutManager);
                //setup adapter
                adapterPdfUser = new AdapterPdfView(getContext(), pdfArrayList);
                //setup adapter to recycleview
                binding.booksRv.setAdapter(adapterPdfUser);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void loadAllBooks() {
        //init list
        pdfArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before add data to arraylist
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    //add to list
                    pdfArrayList.add(model);

                }
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
                binding.booksRv.setLayoutManager(layoutManager);
                //setup adapter
                adapterPdfUser = new AdapterPdfView(getContext(), pdfArrayList);
                //setup adapter to recycleview
                binding.booksRv.setAdapter(adapterPdfUser);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
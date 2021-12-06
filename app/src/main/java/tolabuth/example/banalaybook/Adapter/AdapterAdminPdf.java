package tolabuth.example.banalaybook.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Activities.EditAndDeleteAdminActivity;
import tolabuth.example.banalaybook.MyApplication;
import tolabuth.example.banalaybook.Activities.PdfDetailActivity;
import tolabuth.example.banalaybook.databinding.RowPdfAdminBinding;
import tolabuth.example.banalaybook.model.FilterPdfAdmin;
import tolabuth.example.banalaybook.model.ModelPdf;

public class AdapterAdminPdf extends RecyclerView.Adapter<AdapterAdminPdf.AdminPdfViewHolder>implements Filterable {

    private static final long MAX_BYTES_PDF =50000000 ;
    private static final String TAG = "PDF_ADAPTER_TAG";

    private Context context;
    public ArrayList<ModelPdf>modelPdfs,filterList;
    private RowPdfAdminBinding binding;
    private FilterPdfAdmin filter;
    //declare progressBar
    private ProgressDialog progressDialog;

    public AdapterAdminPdf(Context context, ArrayList<ModelPdf> modelPdfs) {
        this.context = context;
        this.modelPdfs = modelPdfs;
        this.filterList = modelPdfs;
        //initial progressBar
        progressDialog =new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


    }

    @NonNull
    @Override
    public AdminPdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdminPdfViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull AdminPdfViewHolder holder, int position) {
        ModelPdf model = modelPdfs.get(position);
        String title= model.getTitle();
        String description = model.getDescription();
        long timestamp = Long.parseLong(model.getId());
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String pdfUrl = model.getUrl();

        //change timestamp to date
        String formattedDate = MyApplication.formatTimeStamp(timestamp);
        //set Data
        holder.title.setText(title);
        holder.description.setText(description);
        holder.date.setText(formattedDate);


        //laod further details like category , pdf from url,pdf size in separate functions
        //loadCategory(model,holder);
        MyApplication.loadCategory(
                ""+categoryId
                ,holder.category
        );


        //loadPdfFromUrl(model, holder);
        MyApplication.loadPdfFromUrl(
                ""+pdfUrl
                ,""+title
                ,holder.pdfView
                ,holder.progressBar
                ,null
        );

       // loadPdfSize(model, holder);
        MyApplication.loadPdfSize(
                ""+pdfUrl
                ,""+title
                ,holder.size
        );
        //handle click item for to detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

        //handle for create dialog page for to delete and update
        holder.IBMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreOptionDialog(model, holder);
            }
        });
        //code for to handle click , create dialog page for to delete and update
        //handler click to IBMore have to action edit and delete
//        holder.IBMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), view);
//                popupMenu.getMenuInflater().inflate(R.menu.menu_event, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                    switch (menuItem.getItemId()){
//                        case R.id.menu_update:
//                            Intent intent = new Intent(context.getApplicationContext() , EditAndDeleteAdminActivity.class);
//                            intent.putExtra("title",model.getTitle());
//                            intent.putExtra("description",model.getDescription());
//                            intentcategory(model, intent);
//                           // intent.putExtra("category", model.getCategoryId());
//                            context.startActivity(intent);
//                            break;
//
//                        case R.id.menu_delete:
//
//
//
//                    }
//                        return false;
//                    }
//                });
//                        popupMenu.show();
//
//
//            }
//        });
//
//


    }

    private void MoreOptionDialog(ModelPdf model, AdminPdfViewHolder holder) {
        //declare variable from to get database for to show in EditText
        String bookId = model.getId();
        String bookTitle = model.getTitle();
        String bookDescription = model.getDescription();
        String bookUrl = model.getUrl();

        //create option of delete and update
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    //handle options click
                        //handle check click for Edit
                        if (i==0){
                            //Edit click, Open new activity to edit the book information
                            Intent intent = new Intent(context, EditAndDeleteAdminActivity.class);
                            intent.putExtra("bookId",bookId);
                            //intent.putExtra("title",bookTitle);
                           // intent.putExtra("description",bookDescription);
                            context.startActivity(intent);

                        }else if(i==1){
                            //delete clicks
                            MyApplication.deleteBook(context,
                                    ""+bookId
                                    ,""+bookUrl
                                    ,""+bookDescription);
                            //deleteBook(model, holder);
                        }
                    }
                }).show();
    }

//code intent data to edtiand Delete activity
//    private void intentcategory(ModelPdf model, Intent intent) {
//        //get category using categoryId
//        String categoryId = model.getCategoryId();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
//        ref.child(categoryId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        //get category from table categories of firebase
//                        String category = ""+snapshot.child("category").getValue();
//                        //set category to text view
//                      intent.putExtra("category",category);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(context, "Load Category data from firebase",Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "onCancelled: "+error.getMessage());
//
//                    }
//                });
//
//    }





    @Override
    public int getItemCount() {
        return modelPdfs.size();
    }

    @Override
    public Filter getFilter() {
        if (filter ==null){
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }

    public class AdminPdfViewHolder extends RecyclerView.ViewHolder {
        PDFView pdfView;
        TextView title, description, category, size, date;
        ProgressBar progressBar;
        ImageView IBMore;

        public AdminPdfViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfView = binding.pdfAdminIcon;
            title = binding.tvAdminRowPdfTitel;
            description = binding.tvAdminRowPdfDescription;
            progressBar = binding.proAdminIcon;
            category = binding.tvAdminRowPdfCategory;
            size = binding.tvAdminPdfSize;
            date = binding.tvAdminPdfDate;
            IBMore = binding.IBMore;


        }
    }
}

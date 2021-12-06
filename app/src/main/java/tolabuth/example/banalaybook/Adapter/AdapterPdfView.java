package tolabuth.example.banalaybook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Activities.PdfDetailActivity;
import tolabuth.example.banalaybook.MyApplication;
import tolabuth.example.banalaybook.databinding.RowPdfUserBinding;
import tolabuth.example.banalaybook.model.FilterPdfUser;
import tolabuth.example.banalaybook.model.ModelPdf;

public class AdapterPdfView extends RecyclerView.Adapter<AdapterPdfView.HolderPdfUser>implements Filterable {
    private RowPdfUserBinding binding;
    private Context context;
    public ArrayList<ModelPdf>pdfArrayList,filterList;
    private static final String TAG="ADAPTER_USER_PDF";
    private FilterPdfUser filter;


    public AdapterPdfView(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent, false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        //get data
        ModelPdf mode = pdfArrayList.get(position);
        String bookId = mode.getId();
        String title = mode.getTitle();
       // String description = mode.getDescription();
        String pdfUrl = mode.getUrl();
        String categoryId = mode.getCategoryId();
        long timestemp = mode.getTimestamp();
        //set up timestemp
        String date = MyApplication.formatTimeStamp(timestemp);
        //set data
        holder.tvTitle.setText(title);
       // holder.tvDescription.setText(description);
       // holder.tvDate.setText(date);

        MyApplication.loadPdfFromUrl(""+pdfUrl,""+title,holder.pdfView, holder.progressBar,null);
        //MyApplication.loadCategory(""+categoryId,holder.tvCategory);
        //MyApplication.loadPdfSize(""+pdfUrl,""+title, holder.tvSize);
        //handle click , show pdf details activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter=new FilterPdfUser(filterList,this);
        }
        return filter;
    }


    class HolderPdfUser extends RecyclerView.ViewHolder{
        TextView tvTitle,tvDescription, tvCategory, tvSize, tvDate;
        PDFView pdfView;
        ProgressBar progressBar;

        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            tvTitle = binding.tvAdminRowPdfTitel;
           // tvDescription = binding.tvAdminRowPdfDescription;
//            tvCategory = binding.tvAdminRowPdfCategory;
//            tvSize = binding.tvAdminPdfSize;
//            tvDate = binding.tvAdminPdfDate;
            pdfView = binding.pdfAdminIcon;
            progressBar = binding.proAdminIcon;
        }
    }
}

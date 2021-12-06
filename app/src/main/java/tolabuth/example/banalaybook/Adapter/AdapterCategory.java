package tolabuth.example.banalaybook.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Activities.PdfListAdminActivity;
import tolabuth.example.banalaybook.databinding.RowCategoryBinding;
import tolabuth.example.banalaybook.model.FilterCategory;
import tolabuth.example.banalaybook.model.ModelCategory;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.CategoryViewHolder> implements Filterable {
    private Context context;
    public ArrayList<ModelCategory>categories,filterList;
    private RowCategoryBinding binding;
    //instance of our class
    private FilterCategory filter ;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categories) {
        this.context = context;
        this.categories = categories;
        this.filterList = categories;

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding =   RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CategoryViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        //get data
        ModelCategory modelCategory = categories.get(position);
        String id = modelCategory.getId();
        String category = modelCategory.getCategory();

        String uid = modelCategory.getUdi();
        long timestemp = modelCategory.getTimestemp();
        //set data
        holder.category.setText(category);
        Log.d("category",category);
        binding.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirm delete firebase
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure delete category form firebase")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //begin delete
                                Toast.makeText(context, "Deleting....",Toast.LENGTH_SHORT).show();
                                deleteCategory(modelCategory, holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();


            }
        });
        //handle item click , goto PdfListAdminActivty, also pass pdf category and category
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("categoryId", id);
                intent.putExtra("category", category);
                context.startActivity(intent);
            }
        });


    }

    private void deleteCategory(ModelCategory model, CategoryViewHolder holder) {
        String id =model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    //delete succefully
                        Toast.makeText(context, "Delete successfully",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterCategory(filterList, this);
        }
        return filter;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        //ui view of row_category
        TextView category;
        ImageView btnDelete;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            category = binding.tvCategoryName;
            btnDelete = binding.btndelete;
        }
    }
}

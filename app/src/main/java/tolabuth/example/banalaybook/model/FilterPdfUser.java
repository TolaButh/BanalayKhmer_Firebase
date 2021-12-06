package tolabuth.example.banalaybook.model;

import android.widget.Adapter;
import android.widget.Filter;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterPdfView;
import tolabuth.example.banalaybook.Constants;

public class FilterPdfUser extends Filter {
    //arraylist in which we want to search
   private ArrayList<ModelPdf>filterList;
    //adapter in which filter need to be implement
    private AdapterPdfView adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfView adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //value to be search should not be null//empty
        if (charSequence!= null || charSequence.length()>0) {
            //not null not empty
            //change to uppeercase or lower to avoid case sansitivity
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf> filtermodel = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)) {
                    //search match , add to listj
                    filtermodel.add(filterList.get(i));
                }
            }
            results.count = filtermodel.size();
            results.values = filtermodel;


        }else {
            //empty or null, make original list/result
            results.count = filterList.size();
            results.values = filterList;
        }
        return  results;

    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        //apply filter
        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>)filterResults.values;

        //notify change
        adapterPdfUser.notifyDataSetChanged();

    }
}

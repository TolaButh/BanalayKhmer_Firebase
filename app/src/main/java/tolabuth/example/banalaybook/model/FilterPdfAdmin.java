package tolabuth.example.banalaybook.model;

import android.widget.Filter;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterAdminPdf;
import tolabuth.example.banalaybook.Adapter.AdapterCategory;

public class FilterPdfAdmin extends Filter {
    public  ArrayList<ModelPdf>filterList;
    AdapterAdminPdf adapterAdminPdf;
    //constructor


    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterAdminPdf adapterAdminPdf) {
        this.filterList = filterList;
        this.adapterAdminPdf = adapterAdminPdf;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //value should not be not or Empty
        if(charSequence!= null && charSequence.length()>0){
            //change to uppercase or lower caseto  case
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf>filteredModel = new ArrayList<>();
            for (int i = 0 ; i<filterList.size();i++){
                //valide
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    //add to filtter
                    filteredModel.add(filterList.get(i));

                }
            }
            results.count = filteredModel.size();
            results.values= filteredModel;

        }else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
    adapterAdminPdf.modelPdfs = (ArrayList<ModelPdf>)filterResults.values;
    adapterAdminPdf.notifyDataSetChanged();
    }
}

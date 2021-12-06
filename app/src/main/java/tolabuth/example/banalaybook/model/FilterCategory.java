package tolabuth.example.banalaybook.model;

import android.widget.Filter;

import java.util.ArrayList;

import tolabuth.example.banalaybook.Adapter.AdapterCategory;

public class FilterCategory extends Filter {
    ArrayList<ModelCategory>filterList;
    AdapterCategory adapterCategory;
    //constructor


    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //value should not be not or Empty
        if(charSequence!= null && charSequence.length()>0){
            //change to uppercase or lower caseto  case
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCategory>filteredModel = new ArrayList<>();
            for (int i = 0 ; i<filterList.size();i++){
                //valide
                if (filterList.get(i).getCategory().toUpperCase().contains(charSequence)){
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
    adapterCategory.categories = (ArrayList<ModelCategory>)filterResults.values;
    adapterCategory.notifyDataSetChanged();
    }
}

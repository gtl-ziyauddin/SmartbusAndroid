
package com.nimius.smartbus.views.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nimius.smartbus.R;
import com.nimius.smartbus.views.callback.PopulateListCallback;
import com.nimius.smartbus.views.service.model.PickupInfo.PickupInfoModel;

import java.util.ArrayList;
import java.util.List;


public class PlacesApiArrayAdapter extends ArrayAdapter<PickupInfoModel> implements Filterable {


    private List<PickupInfoModel> itemsAll = new ArrayList<>();
    private PopulateListCallback populateListCallback;

    Context mContext;
    int layoutResourceId;
    private LayoutInflater mLayoutInflater;

    public PlacesApiArrayAdapter(Context mContext, int layoutResourceId, List<PickupInfoModel> data, PopulateListCallback populateListCallback) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        itemsAll.addAll(data);
        this.populateListCallback = populateListCallback;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            if (convertView == null) {
                // inflate the layout
                convertView = mLayoutInflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position

            PickupInfoModel model = getItem(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.tv_places);
            textViewItem.setText(model.title);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((PickupInfoModel) resultValue).title;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            ArrayList<PickupInfoModel> suggestions = new ArrayList<>();
            if (TextUtils.isEmpty(constraint)) {
                results.count = itemsAll.size();
                results.values = new ArrayList(itemsAll);
            }else{
                for (PickupInfoModel customer : itemsAll) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (customer.title.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<PickupInfoModel>) results.values);
            } else {
                // no filter, add entire original list back in
                if (TextUtils.isEmpty(constraint)) {
                    addAll(itemsAll);
                }
//                populateListCallback.populateList();
            }
            notifyDataSetInvalidated();
        }
    };


}
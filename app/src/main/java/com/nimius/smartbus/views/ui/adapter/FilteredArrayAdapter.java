package com.nimius.smartbus.views.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.nimius.smartbus.R;
import com.nimius.smartbus.views.service.model.PickupInfo.PickupInfoModel;

import java.util.ArrayList;
import java.util.List;

public class FilteredArrayAdapter extends ArrayAdapter<PickupInfoModel> {

	private Context context;
	private int resourceId;
	private List<PickupInfoModel> items, tempItems, suggestions;

	public FilteredArrayAdapter(@NonNull Context context, int resourceId, List<PickupInfoModel> items) {
		super(context, resourceId, items);
		this.items = items;
		this.context = context;
		this.resourceId = resourceId;
		tempItems = new ArrayList<>(items);
		suggestions = new ArrayList<>();
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		try {
			if (convertView == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				view = inflater.inflate(resourceId, parent, false);
			}
			// object item based on the position

			PickupInfoModel model = getItem(position);

			// get the TextView and then set the text (item name) and tag (item ID) values
			TextView textViewItem = (TextView) convertView.findViewById(R.id.tv_places);
			textViewItem.setText(model.title);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Nullable
	@Override
	public PickupInfoModel getItem(int position) {
		return items.get(position);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@NonNull
	@Override
	public Filter getFilter() {
		return PickupInfoModelFilter;
	}

	private Filter PickupInfoModelFilter = new Filter() {
		@Override
		public CharSequence convertResultToString(Object resultValue) {
			PickupInfoModel PickupInfoModel = (PickupInfoModel) resultValue;
			return PickupInfoModel.title;
		}

		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			if (charSequence != null) {
				suggestions.clear();
				for (PickupInfoModel PickupInfoModel: tempItems) {
					if (PickupInfoModel.title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
						suggestions.add(PickupInfoModel);
					}
				}

				FilterResults filterResults = new FilterResults();
				filterResults.values = suggestions;
				filterResults.count = suggestions.size();
				return filterResults;
			} else {
				return new FilterResults();
			}
		}

		@Override
		protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
			ArrayList<PickupInfoModel> c = (ArrayList<PickupInfoModel>) filterResults.values;
			if (filterResults != null && filterResults.count > 0) {
				clear();
				for (PickupInfoModel cust : c) {
					add(cust);
					notifyDataSetChanged();
				}
			} else {
				clear();
				notifyDataSetChanged();
			}
		}
	};
}
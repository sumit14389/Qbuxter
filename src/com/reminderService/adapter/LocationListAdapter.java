package com.reminderService.adapter;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.reminderService.VO.LocationVO;
import com.reminderService.activity.R;
import com.reminderService.activity.TakeTicketActivity;
import com.reminderService.constant.Constant;

import eu.erikw.PullToRefreshListView;

public class LocationListAdapter extends BaseAdapter implements Filterable{
	int resourceId;
	Context mContext;
	ArrayList<LocationVO> gridAdapterDataList1;
	private Typeface tf;
	//	HashMap<Integer, String> mIdMapLocation = new HashMap<Integer, String>();
	//	HashMap<Integer, String> mIdMapDistance = new HashMap<Integer, String>();
	//	List<String> value1,value2;
	private PullToRefreshListView lvlistlocation;
	int tag;
	private String active;
	private ArrayList<LocationVO> dublicateDataList;
	private LocationVO dataItem;
	private LocationVO locationVO;

	public LocationListAdapter(Context mContext,int customListItem, PullToRefreshListView locationList, ArrayList<LocationVO> list1) {
		this.mContext=mContext;
		this.resourceId=customListItem;
		gridAdapterDataList1=list1;
		dublicateDataList=list1;
		this.lvlistlocation=locationList;

	}



	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder viewHolder;
		locationVO=(LocationVO)Constant.locationArray.get(position);

		dataItem=dublicateDataList.get(position);
		active=dataItem.getIsActive();
		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);

			viewHolder = new ViewHolder();
			tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");


			viewHolder.locationTxtView = (TextView)convertView.findViewById(R.id.tvLocation);
			viewHolder.locationTxtView.setTypeface(tf);
			viewHolder.milesTxtView = (TextView)convertView.findViewById(R.id.tvMiles);
			viewHolder.milesTxtView.setTypeface(tf);

			lvlistlocation.setOnItemClickListener(new OnItemClickListener() {

				private Intent intent;
				private LocationVO dataItemclick;
				private String presentDataActive;


				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {

					dataItemclick=dublicateDataList.get(arg2);
					presentDataActive=(dataItemclick.getIsActive());
					if(presentDataActive.equalsIgnoreCase("1")) 
					{
						intent = new Intent(mContext, TakeTicketActivity.class);
						intent.putExtra("location_name", dataItemclick.getLocation_name());
						intent.putExtra("location_id", dataItemclick.getId());
						mContext.startActivity(intent);
					}
					else if (presentDataActive.equalsIgnoreCase("0")) {
						Toast.makeText(mContext, "Location Inactive", Toast.LENGTH_SHORT).show();
					}
				}
			});

			convertView.setTag(viewHolder);
			//			notifyDataSetChanged();
		} else {

			viewHolder = (ViewHolder)convertView.getTag();

			if(active.equalsIgnoreCase("1")) {
				viewHolder.locationTxtView.setBackgroundResource(R.drawable.location_bg);
				viewHolder.milesTxtView.setBackgroundResource(R.drawable.mile_bg);
			}
		}	
		//TransactionHistoryVO transHistVO = (TransactionHistoryVO)gridAdapterDataList.get(position);


		if(active.equalsIgnoreCase("0")) 
		{
			viewHolder.locationTxtView.setBackgroundResource(R.drawable.location_bg_shade);
			viewHolder.milesTxtView.setBackgroundResource(R.drawable.mile_bg_shade);
		}


		viewHolder.locationTxtView.setText(dataItem.getLocation_name());
		viewHolder.milesTxtView.setText(dataItem.getMiles()+" Miles");
		//		notifyDataSetChanged();


		return convertView;
	}



	private class ViewHolder
	{
		TextView locationTxtView;
		TextView milesTxtView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dublicateDataList.size();
	}


	public LocationVO getItem(int position) {
		// TODO Auto-generated method stub
		return dublicateDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("DefaultLocale")
	public Filter getFilter() {
		Filter filter = new Filter() {

			private ArrayList<String> mDisplayedValues;

			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint,FilterResults results) {

				//				mDisplayedValues = (ArrayList<String>) results.values; // has the filtered values
				dublicateDataList = (ArrayList<LocationVO>) results.values;

				notifyDataSetChanged();// notifies the data with new filtered values
			}

			@SuppressLint("DefaultLocale")
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
				ArrayList<LocationVO> FilteredArrList = new ArrayList<LocationVO>();

				if (gridAdapterDataList1 == null) {
					gridAdapterDataList1 = new ArrayList<LocationVO>(dublicateDataList); // saves the original data in mOriginalValues
				}

				/********
				 * 
				 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
				 *  else does the Filtering and returns FilteredArrList(Filtered)  
				 *
				 ********/
				if (constraint == null || constraint.length() == 0) {

					// set the Original result to return  
					results.count = gridAdapterDataList1.size();
					results.values = gridAdapterDataList1;
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < gridAdapterDataList1.size(); i++) {
						String data = gridAdapterDataList1.get(i).getLocation_name();
						if (data.toLowerCase().startsWith(constraint.toString())) {
							//String location_name, String miles,String success,
							//String id,String user_id,String isActive,String duty_hour_start,String duty_hour_end,String latitude,String longitude
							FilteredArrList.add(new LocationVO(gridAdapterDataList1.get(i).getLocation_name(),
									gridAdapterDataList1.get(i).getMiles(),gridAdapterDataList1.get(i).getId(),gridAdapterDataList1.get(i).getUser_id(),
									gridAdapterDataList1.get(i).getIsActive(),gridAdapterDataList1.get(i).getDuty_hour_start(),gridAdapterDataList1.get(i).getDuty_hour_end(),
									gridAdapterDataList1.get(i).getLatitude(),gridAdapterDataList1.get(i).getLongitude()));
							//							FilteredArrList.add(new LocationVO(gridAdapterDataList1.get(i).getLocation_name()));
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					System.out.println("array size in filter: "+results.count);
					results.values = FilteredArrList;
				}           
				return results;
			}
		};
		return filter;
	}
}
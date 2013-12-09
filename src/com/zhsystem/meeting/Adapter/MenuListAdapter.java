package com.zhsystem.meeting.Adapter;

import java.util.List;
import com.zhsystem.meeting.Bean.MenuBean;
import com.zhsystem.meeting.Main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<MenuBean> dataList;
	private MenuBean bean;
	public MenuListAdapter(Context context,List<MenuBean> dataList){
		this.context=context;
		this.dataList=dataList;
		this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.menulist_item, null);
			viewHolder = new ViewHolder();
		
			viewHolder.title=(TextView)convertView.findViewById(R.id.mune_title_txt);
			viewHolder.image_icon=(ImageView) convertView.findViewById(R.id.menu_image);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		bean=dataList.get(position);
		viewHolder.image_icon.setBackgroundResource(bean.getImageID());
		viewHolder.title.setText(bean.getTitle());
		return convertView;
	}
	
	private class ViewHolder
	{		
		TextView title;
		ImageView image_icon;
	}

	
}


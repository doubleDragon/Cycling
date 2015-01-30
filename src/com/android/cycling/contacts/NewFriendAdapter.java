package com.android.cycling.contacts;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.UpdateListener;

import com.android.cycling.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewFriendAdapter extends ArrayAdapter<BmobInvitation>{
	
	private final LayoutInflater mLayoutInflater;
	
	private DisplayImageOptions options;
	
	private Context mContext;

	public NewFriendAdapter(Context context) {
		super(context, R.layout.new_friend_item);

		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bmob_default_head)
				.showImageForEmptyUri(R.drawable.bmob_default_head)
				.showImageOnFail(R.drawable.bmob_default_head).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void setData(List<BmobInvitation> items) {
		clear();

        if (items == null) return;
        addAll(items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BmobInvitation invitation = getItem(position);
        View result;
        NewFriendListItemViewCache viewCache;
        if (convertView != null) {
            result = convertView;
            viewCache = (NewFriendListItemViewCache) result.getTag();
        } else {
            result = mLayoutInflater.inflate(R.layout.new_friend_item, parent, false);
            viewCache = new NewFriendListItemViewCache(result);
            result.setTag(viewCache);
        }
        
        ImageLoader.getInstance().displayImage(invitation.getAvatar(), viewCache.avatar, options);
        
        int status = invitation.getStatus();
        if(status==BmobConfig.INVITE_ADD_NO_VALIDATION||status==BmobConfig.INVITE_ADD_NO_VALI_RECEIVED){
			viewCache.add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					agressAdd((Button)arg0, invitation);
				}
			});
		}else if(status==BmobConfig.INVITE_ADD_AGREE){
			viewCache.add.setText("已同意");	
			viewCache.add.setBackgroundDrawable(null);
			viewCache.add.setTextColor(Color.parseColor("#3e3e39"));
			viewCache.add.setEnabled(false);
		}
        viewCache.name.setText(invitation.getFromname());
        
		return result;
	}
	
	private void agressAdd(final Button btn_add,final BmobInvitation msg){
		final ProgressDialog progress = new ProgressDialog(mContext);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		try {
			//ͬ����Ӻ���
			BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					progress.dismiss();
					btn_add.setText("已同意");
					btn_add.setTextColor(Color.parseColor("#3e3e39"));
					btn_add.setEnabled(false);
//					CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));	
				}
				
				@Override
				public void onFailure(int arg0, final String arg1) {
					progress.dismiss();
					Log.d("tag", "添加失败 error: " + arg1);
				}
			});
		} catch (final Exception e) {
			progress.dismiss();
			Log.d("tag", "添加失败 error: " + e.getMessage());
		}
	}
	
	private static class NewFriendListItemViewCache {
		public final TextView name;
		public final ImageView avatar;
		public final Button add;
		public NewFriendListItemViewCache(View view) {
			avatar = (ImageView) view.findViewById(R.id.avatar);
			name = (TextView) view.findViewById(R.id.name);
			add = (Button) view.findViewById(R.id.btn_add);
		}
	}


}

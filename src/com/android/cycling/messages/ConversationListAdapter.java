package com.android.cycling.messages;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.android.cycling.R;
import com.android.cycling.util.FaceTextUtils;
import com.android.cycling.util.ImageLoadOptions;
import com.android.cycling.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ConversationListAdapter extends ArrayAdapter<BmobRecent> implements
		Filterable {

	private final LayoutInflater mLayoutInflater;
	private Context mContext;

	public ConversationListAdapter(Context context, int resource) {
		super(context, resource);

		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void setData(List<BmobRecent> items) {
		clear();

		if (items == null)
			return;
		addAll(items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BmobRecent item = getItem(position);
		View result;
		ConversationListItemCache viewCache;
		if (convertView != null) {
			result = convertView;
			viewCache = (ConversationListItemCache) result.getTag();
		} else {
			result = mLayoutInflater.inflate(R.layout.conversation_list_item,
					parent, false);
			viewCache = new ConversationListItemCache(result);
			result.setTag(viewCache);
		}

		ImageLoader.getInstance().displayImage(item.getAvatar(),
				viewCache.recent_avatar, ImageLoadOptions.getOptions());

		viewCache.recent_name.setText(item.getUserName());
		viewCache.recent_time.setText(TimeUtil.getChatTime(item.getTime()));
		if (item.getType() == BmobConfig.TYPE_TEXT) {
			SpannableString spannableString = FaceTextUtils.toSpannableString(
					mContext, item.getMessage());
			viewCache.recent_msg.setText(spannableString);
		} else if (item.getType() == BmobConfig.TYPE_IMAGE) {
			viewCache.recent_msg.setText(R.string.picture);
		} else if (item.getType() == BmobConfig.TYPE_LOCATION) {
			String all = item.getMessage();
			if (all != null && !all.equals("")) {
				String address = all.split("&")[0];
				String addressLabel = mContext.getString(R.string.bmob_location, address);
				viewCache.recent_msg.setText(addressLabel);
//				viewCache.recent_msg.setText("[位置]" + address);
			}
		} else if (item.getType() == BmobConfig.TYPE_VOICE) {
			viewCache.recent_msg.setText(R.string.bmob_voice);
		}

		int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
		if (num > 0) {
			viewCache.recent_unread.setVisibility(View.VISIBLE);
			viewCache.recent_unread.setText(num + "");
		} else {
			viewCache.recent_unread.setVisibility(View.GONE);
		}

		return result;
	}

	public static class ConversationListItemCache {

		public final ImageView recent_avatar;
		public final TextView recent_name;
		public final TextView recent_msg;
		public final TextView recent_time;
		public final TextView recent_unread;

		public ConversationListItemCache(View v) {
			recent_avatar = (ImageView) v.findViewById(R.id.iv_recent_avatar);
			recent_name = (TextView) v.findViewById(R.id.tv_recent_name);
			recent_msg = (TextView) v.findViewById(R.id.tv_recent_msg);
			recent_time = (TextView) v.findViewById(R.id.tv_recent_time);
			recent_unread = (TextView) v.findViewById(R.id.tv_recent_unread);
		}

	}

}

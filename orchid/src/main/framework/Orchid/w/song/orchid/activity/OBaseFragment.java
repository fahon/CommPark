package w.song.orchid.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import w.song.orchid.util.MyTools;
import w.song.orchid.util.OSharedPreferencesHelper;
import willsong.cn.orchid.R;

public class OBaseFragment extends Fragment {
	public View mContainer;
	public Context mContext;
	public LayoutInflater mInflater;
	protected Intent mIntent;
	//protected BusinessManager mBusinessManager;
	protected OSharedPreferencesHelper mOSharedPreferencesHelper;
	protected DisplayImageOptions displayImageOptions;
	protected Gson mGson;
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		mContext=getActivity();
		mInflater=LayoutInflater.from(mContext);		
		mIntent = getActivity().getIntent();
		mGson = new Gson();
		mOSharedPreferencesHelper = new OSharedPreferencesHelper(getActivity());
		//mBusinessManager = new BusinessManager(this);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.ic_launcher).showImageForEmptyUri(R.mipmap.ic_launcher)
				.showImageOnFail(R.mipmap.ic_launcher).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public Intent intentInstance(Context context, Class<?> clazz) {
		return new Intent(context, clazz);
	}

	public Intent intentInstance(Class<?> clazz) {
		return new Intent(mContext, clazz);
	}

	public void refreshView(String type) {

	};

	public void refreshView(String type, String json) {

	};

	public void refreshView(String type, String code, String codeInfo, String json) {

	};

	public void refreshView(String type, ArrayList<String[]> list) {

	};

	public void refreshView(String type, List<Map<String, Object>> list) {

	};
	
	public void refreshView(String type, Map<String, Object> Map) {

	};
	
	public void refreshView(String type, String[] strs) {

	};
	
	public void refreshView(String type,  Map<String,Object> tagMap,String json) {

	};
	
	public void refreshView(String type,  Map<String,Object> tagMap,Object value) {

	};
	public void refreshView(String type,  Map<String,Object> tagMap,Map<String,Object> valueMap) {

	};
	
	public void refreshViewForFail(String type,  Map<String,Object> tagMap) {

	};
	
	protected static class AnimateFirstDisplayDefultListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		public AnimateFirstDisplayDefultListener() {

		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	public void displayImage(String url, ImageView imageView) {
		ImageLoader.getInstance().displayImage(url, imageView, displayImageOptions, new AnimateFirstDisplayDefultListener());
	}
	
	public String getIntentValue(String key, String defultValue) {
		return MyTools.replaceNull(mIntent.getStringExtra(key), defultValue);
	}
}
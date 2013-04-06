package nomula.Nico;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListTemplateAdapter extends SimpleAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Map<String, Object>> items;

	public ListTemplateAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	void setListData(ArrayList<Map<String, Object>> data){
		items = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;

		if (v == null)
			v = inflater.inflate(R.layout.column, null);

		Map<String, Object> settingData = items.get(position);

		TextView textUrl = (TextView)v.findViewById(R.id.TextUrl);
		ImageView imageView = (ImageView)v.findViewById(R.id.ImageThumb);
		ProgressBar waitBar = (ProgressBar)v.findViewById(R.id.WaitBar);

		waitBar.setVisibility(View.VISIBLE);
		imageView.setVisibility(View.GONE);

		textUrl.setText(settingData.get("url").toString());
		String thumbUrl = settingData.get("viewImage").toString();

		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon));

		try{
			imageView.setTag(thumbUrl);
			ImageGetTask task = new ImageGetTask(imageView, waitBar);
			task.execute(thumbUrl);
		} catch (Exception e){
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon));
			waitBar.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);
		}

		return v;
	}

	class ImageGetTask extends AsyncTask<String, Void, Bitmap>{
		private ImageView image;
		private ProgressBar progress;
		private String tag;

		public ImageGetTask(ImageView _image, ProgressBar _progress){
			image = _image;
			progress = _progress;
			tag = image.getTag().toString();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			synchronized (context) {
				try{
					Bitmap image = ImageCache.getImage(params[0]);
					if (image ==null){
						URL imageUrl = new URL(params[0]);
						InputStream imageIs;
						imageIs = imageUrl.openStream();
						image = BitmapFactory.decodeStream(imageIs);
						ImageCache.setImage(params[0], image);
					}
					return image;
				} catch (Exception e){
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(Bitmap result){
			if (tag.equals(image.getTag())){
				if (result != null)
					image.setImageBitmap(result);
				else
					image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon));
			}

			progress.setVisibility(View.GONE);
			image.setVisibility(View.VISIBLE);
		}
	}


}

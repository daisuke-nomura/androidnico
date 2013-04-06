package nomula.Nico;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import nomula.Nico.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultListAdapter extends ArrayAdapter<RankingResults>{
    private List<RankingResults> items;
    private LayoutInflater inflater;
    Drawable[] icons;

    //コンストラクタ
    public ResultListAdapter(Context context, int resourceId, List<RankingResults> items) {
        super(context, resourceId, items);
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        icons = new Drawable[items.size()];

        for (int i = 0; i < items.size(); i++){
        	try {
    			//ここは複数スレッドが動くようにすべき
        			URL uri = new URL(items.get(i).getThumbnail());

					HttpURLConnection http = (HttpURLConnection)uri.openConnection();
					http.setRequestMethod("GET");
					http.setConnectTimeout(50);
					http.connect();
        			icons[i] = Drawable.createFromStream(http.getInputStream(), "");
					http.disconnect();
        	} catch(Exception e){
        		icons[i] = null;
        		e.printStackTrace();
        	}
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            //1行分layoutからViewの塊を生成
            v = inflater.inflate(R.layout.row2, null);
        }
        //itemsからデータ
        //vから画面にくっついているViewを取り出して値をマッピングする
        RankingResults ranking = items.get(position);

//        Bitmap bitmap = null;
//        try {
//			HttpURLConnection huc = ((HttpURLConnection)(new URL(ranking.getThumbnail())).openConnection());
//			huc.setDoInput(true);
//			huc.connect();
//
//			InputStream is = huc.getInputStream();
//			bitmap = BitmapFactory.decodeStream(is);
//		} catch (MalformedURLException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//
        ImageView iv = (ImageView)v.findViewById(R.id.imageView1);
//        iv.setImageBitmap(bitmap);
        if (icons != null)
        	iv.setImageDrawable(icons[position]);

//        WebView wv = (WebView)v.findViewById(R.id.webViewz);
//        String data = "<html><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /><body style=\"margin:0px;\"><img style=\"width:130px;height:100px;\" src=\"" + ranking.getThumbnail() + "\" /></body></html>";
////        wv.loadUrl(data);
//        wv.loadData(data, "text/html", "UTF-8");
////        wv.setClickable(true);
//
//        wv.loadUrl(ranking.getThumbnail());



        TextView hoge = (TextView)v.findViewById(R.id.row_text2);
        hoge.setText(ranking.getTitle());
        hoge.setTextColor(Color.parseColor("#408BEF"));
        hoge.setTextSize(14);
        hoge.setGravity(Gravity.CENTER_VERTICAL);

        return v;
    }
}
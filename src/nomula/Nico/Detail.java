package nomula.Nico;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nomula.Nico.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Detail extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        final Intent intent = getIntent();

        TextView tv = (TextView)findViewById(R.id.textView1);
        tv.setText(intent.getStringExtra("Title"));

        if (!intent.getStringExtra("MovieType").equals("mp4")){
        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout1);
        	ll.setBackgroundColor(Color.RED);
        }

        tv.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			if (!intent.getStringExtra("MovieType").equals("mp4"))
    				return;

				SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
				String history = sp.getString("his", "");

				if (!history.contains(intent.getStringExtra("ID"))){
					String[] historySize = history.split(",");

					if (historySize.length > 19){
						StringBuilder sb = new StringBuilder();
						sb.append(intent.getStringExtra("ID"));
						sb.append(",");
						for (int i = 0; i < 19; i++){
							sb.append(historySize[i]);
							sb.append(",");
						}
						history = sb.toString();
					} else
						history = intent.getStringExtra("ID") + "," + history;

					Editor e = sp.edit();
					e.putString("his", history);
					e.commit();
				}

    			Intent playerIntent = new Intent(Detail.this, Player.class);
    			playerIntent.putExtra("ID", intent.getStringExtra("ID"));
    			playerIntent.putExtra("Title", intent.getStringExtra("Title"));
    			playerIntent.putExtra("Description", intent.getStringExtra("Description"));
    			playerIntent.putExtra("Length", intent.getStringExtra("Length"));
    			playerIntent.putExtra("MovieType", intent.getStringExtra("MovieType"));
    			playerIntent.putExtra("Thumbnail", intent.getStringExtra("Thumbnail"));
    			playerIntent.putExtra("Tag", intent.getStringExtra("Tag"));
    			playerIntent.putExtra("SessionID", intent.getStringExtra("SessionID"));
    			startActivity(playerIntent);
    		}
    	});

        TextView tv2 = (TextView)findViewById(R.id.textView2);
        tv2.setText("再生数:" + intent.getIntExtra("ViewCount", 0));
        TextView tv3= (TextView)findViewById(R.id.textView3);
        tv3.setText("コメント数:" + intent.getIntExtra("CommentCount", 0));
        TextView tv4 = (TextView)findViewById(R.id.textView4);
        tv4.setText("マイリスト数:" + intent.getIntExtra("MylistCount", 0));
        TextView tv7 = (TextView)findViewById(R.id.textView7);
        tv7.setText("投稿日時:" + intent.getStringExtra("UploadTime"));

        TextView tv5 = (TextView)findViewById(R.id.textView5);
        String des = intent.getStringExtra("Description");
        des.replace("<br />", "\n");
        des.replace("<br>", "\n");
        tv5.setText(des);

        TextView tv6 = (TextView)findViewById(R.id.textView6);
//        String tag = intent.getStringExtra("Tag");
//        String[] tagList = tag.split(" ");

        tv6.setText(intent.getStringExtra("Tag"));

        String str = intent.getStringExtra("Thumbnail");
        WebView wv = (WebView)findViewById(R.id.webView);
        String data = "<html><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /><body style=\"margin:0px;\"><img style=\"width:169px;height:130px;\" src=\"" + str + "\" /></body></html>";
        wv.loadData(data, "text/html", "UTF-8");


        Button b = (Button)findViewById(R.id.toriaezuButton);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String target = "http://i.nicovideo.jp/v2/deflist.add?sid=" + intent.getStringExtra("SessionID") + "&vid=" + intent.getStringExtra("ID");
				URL uri;
				String res;
				try {
					uri = new URL(target);
					HttpURLConnection http = (HttpURLConnection)uri.openConnection();
					http.setRequestMethod("GET");
					http.connect();
					InputStream in = http.getInputStream();
					byte b[] = new byte[1024];
					in.read(b);
					in.close();
					http.disconnect();
					res = new String(b);

					if (res.contains("ok"))
						Toast.makeText(Detail.this, "登録しました", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(Detail.this, "登録失敗", Toast.LENGTH_SHORT).show();

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

        Button b1 = (Button)findViewById(R.id.twitterButton);
        b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");

				intent.putExtra(Intent.EXTRA_TEXT, "aaaa");
				startActivity(Intent.createChooser(intent, "eeee"));
			}
		});
    }
}
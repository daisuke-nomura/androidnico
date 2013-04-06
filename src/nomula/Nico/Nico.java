package nomula.Nico;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import nomula.Nico.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class Nico extends Activity implements View.OnTouchListener {
    private ViewFlipper viewFlipper;
    private float firstTouch;
    private boolean isFlip = false;
    List<Ranking> list = null;
    List<RankingResults> result = null;
    private static String sessionID;
    private boolean logined = false;
    public static boolean pcLogined = false;
    ArrayList<MyListSummary> mylistGroup = new ArrayList<MyListSummary>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        findViewById(R.id.layout_first).setOnTouchListener(this);
        findViewById(R.id.layout_second).setOnTouchListener(this);
        findViewById(R.id.layout_third).setOnTouchListener(this);
        findViewById(R.id.layout_fourth).setOnTouchListener(this);

        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText)findViewById(R.id.mailText);
				EditText et1 = (EditText)findViewById(R.id.passText);
				SpannableStringBuilder sb = (SpannableStringBuilder)et.getText();
				SpannableStringBuilder sb1 = (SpannableStringBuilder)et1.getText();
				SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);

				Editor e = sp.edit();
				e.putString("mail", sb.toString());
				e.putString("pass",  sb1.toString());
				e.commit();

				if (!sp.getString("mail", "").equals("") && !sp.getString("pass", "").equals("")){
					try {
						loginRequest();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

        SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
        if (!sp.getString("mail", "").equals("") && !sp.getString("pass", "").equals("")){
        	EditText et = (EditText)findViewById(R.id.mailText);
        	et.setText(sp.getString("mail", ""));
        }
//        EditText editText = (EditText)findViewById(R.id.editText1);
//        editText.setKeyListener(new KeyListener() {
//			@Override
//			public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_ENTER)
//					return true;
//				else
//					return false;
//			}
//
//			@Override
//			public void clearMetaKeyState(View view, Editable content,
//					int states) {
//				// TODO 自動生成されたメソッド・スタブ
//
//			}
//
//			@Override
//			public int getInputType() {
//				// TODO 自動生成されたメソッド・スタブ
//				return 0;
//			}
//
//			@Override
//			public boolean onKeyDown(View view, Editable text, int keyCode,
//					KeyEvent event) {
//				// TODO 自動生成されたメソッド・スタブ
//
//				if (keyCode == KeyEvent.KEYCODE_ENTER)
//					return true;
//				else
//					return false;
//			}
//
//			@Override
//			public boolean onKeyOther(View view, Editable text, KeyEvent event) {
//				// TODO 自動生成されたメソッド・スタブ
//				return false;
//			}
//		});

//        editText.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//		        EditText editText = (EditText)findViewById(R.id.editText1);
//				editText.setFocusable(true);
//			}
//		});

        list = new ArrayList<Ranking>();
        String res = null;
        try {
			URL uri = new URL("http://i.nicovideo.jp/v2/genre.list?overseas=0&official=0");
			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
			http.setRequestMethod("GET");
			http.connect();

			InputStream in = http.getInputStream();
			byte b1[] = new byte[8192];
			in.read(b1);
			in.close();
			http.disconnect();
			res = new String(b1);
        	xmlRankingParser(res);
		} catch (Exception e) {
			e.printStackTrace();
		}

        nomula.Nico.ListAdapter adapter = new nomula.Nico.ListAdapter(Nico.this, R.layout.row, list);
        ListView lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        if (!sp.getString("mail", "").equals("") && !sp.getString("pass", "").equals("")){
	        try {
				loginRequest();
			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}


	        lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					Ranking resp = list.get(position);
					TextView tv = (TextView)findViewById(R.id.resultText);
					tv.setText("カテゴリ：" + resp.getTitle());

					try {
						URL uri = null;
						if (resp.getTitleId().equals("all"))
							uri = new URL("http://i.nicovideo.jp/v2/video.ranking?type=mylist&limit=20&span=hourly&genre=all");
						else
							uri = new URL("http://i.nicovideo.jp/v2/video.ranking?type=mylist&limit=20&span=daily&genre=" + resp.getTitleId());
	//					uri = new URL("http://49.212.14.82/result.xml");
						HttpURLConnection http = (HttpURLConnection)uri.openConnection();
						http.setRequestMethod("GET");
						http.connect();

						listCallback(http.getInputStream());
						http.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	        });
        }
    }

    private void loginRequest() throws Exception{
		String res = null, ticket = null, res2 = null, res3 = null;

    	if (logined == false){
			URL uri = new URL("https://secure.nicovideo.jp/secure/login?site=nicoiphone");
			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", "iPod touch");
			http.setRequestProperty("Accept-Encoding", "gzip, deflate");
			http.setDoOutput(true);

			OutputStream os = http.getOutputStream();
			PrintStream ps = new PrintStream(os);
			SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
			String mail = sp.getString("mail", "");
			String pass = sp.getString("pass", "");
			ps.print("mail=" + mail + "&password=" + pass);
			ps.close();
			os.close();

			InputStream in = http.getInputStream();
			byte b[] = new byte[65536];
			in.read(b);
			in.close();
			http.disconnect();
			res = new String(b);
    	}

    	if (res.contains("ok")){
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(res));
    		Document doc = db.parse(is);
    		NodeList node = doc.getElementsByTagName("nicovideo_user_response");
    		Element element = (Element)node.item(0);
    		ticket = element.getElementsByTagName("ticket").item(0).getTextContent();
    	}

    	if (ticket != null){
			URL uri = new URL("http://i.nicovideo.jp/v2/login?ticket=" + ticket);
			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("User-Agent", "iPod touch");
			http.connect();
			InputStream in = http.getInputStream();
			byte b[] = new byte[65536];
			in.read(b);
			in.close();
			http.disconnect();
			res2 = new String(b);
    	}

    	if (res2 != null){
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(res2));
    		Document doc = db.parse(is);
    		NodeList node = doc.getElementsByTagName("nicovideo_user_response");
    		Element element = (Element)node.item(0);
    		Element userList = (Element)element.getElementsByTagName("user").item(0);
    		sessionID = userList.getElementsByTagName("session_id").item(0).getTextContent();
        	logined = true;
    	}

    	if (Nico.sessionID != null){
			URL uri = new URL("http://i.nicovideo.jp/v2/mylistgroup.get?sid=" + Nico.sessionID + "&detail=1");
			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("User-Agent", "iPod touch");
			http.connect();
			InputStream in = http.getInputStream();
			byte b[] = new byte[65536];
			in.read(b);
			in.close();
			http.disconnect();
			res3 = new String(b);
    	}

    	if (res3 != null){
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(res3));
    		Document doc = db.parse(is);
    		NodeList list = doc.getElementsByTagName("mylistgroup");

    		MyListSummary mylist = new MyListSummary();
    		mylist.setMyListID("0");
    		mylist.setName("履歴");
    		mylistGroup.add(mylist);

    		MyListSummary mylist1 = new MyListSummary();
    		mylist1.setMyListID("1");
    		mylist1.setName("とりあえずマイリスト");
    		mylistGroup.add(mylist1);

    		for (int i = 0; i < list.getLength(); i++){
	    		Element element = (Element)list.item(i);
	    		MyListSummary my = new MyListSummary();
	    		my.setMyListID(element.getElementsByTagName("id").item(0).getTextContent());
	    		my.setName(element.getElementsByTagName("name").item(0).getTextContent());
	    		mylistGroup.add(my);
    		}
    	}

    	if (mylistGroup.size() > 0){
            nomula.Nico.MyListAdapter adapter3 = new nomula.Nico.MyListAdapter(Nico.this, R.layout.row, mylistGroup);
            ListView lv = (ListView)findViewById(R.id.listView3);
            lv.setAdapter(adapter3);

            lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					MyListSummary my = mylistGroup.get(position);
					String str = my.getName();
					URL uri =  null;
		            TextView tv = (TextView)findViewById(R.id.resultText);

					if (str.equals("履歴")){
						tv.setText("マイリスト：履歴");
						SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
						String history = sp.getString("his", "");

						if (!history.equals("")){
							try {
								uri = new URL("http://i.nicovideo.jp/v2/video.array?v=" + history);
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					} else{
						if (str.equals("とりあえずマイリスト")){
							tv.setText("マイリスト：とりあえずマイリスト");
							try {
								uri = new URL("http://i.nicovideo.jp/v2/deflist.list?sort=1&sid=" + Nico.sessionID + "&id=0&limit=20&from=0");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						} else{
							tv.setText("マイリスト：" + str);
							try {
								uri = new URL("http://i.nicovideo.jp/v2/mylistvideo.get?sort=1&sid=" + Nico.sessionID + "&id=" + my.getMyListID() + "&limit=20&from=0");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						HttpURLConnection http = (HttpURLConnection)uri.openConnection();
						http.setRequestMethod("GET");
						http.connect();

//						InputStream in = http.getInputStream();
//						byte b[] = new byte[65536];
//						in.read(b);
//						in.close();
//						String res = new String(b);
//
//						if (result != null && result.size() > 0)
//							result.clear();
//
//						xmlResultParser(res);

//				        WebView wv = (WebView)findViewById(R.id.webView1);
//				        StringBuilder sb = new StringBuilder();
//				        sb.append("<html><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /><meta charset=\"UTF-8\"><script type=\"text/javascript\">window.onload = function(){}</script><body style=\"margin:0px;\">");
//				        sb.append("<button id=\"btn\" onclick=\"alert(\"hello\");\">click</button>");
//
//				        for (int i = 0; i < result.size(); i++){
//				        	sb.append("<section style=\"margin-bottom:10px;\"><a href=\"aaaaaaaa\"><img src=\"");
//				        	sb.append(result.get(i).getThumbnail());
//				        	sb.append("\" float=\"left\" /></a><span style=\"color:#408BEF;vertical-align:middle;font-size:18px;\"><a href=\"aaaaaaaa\">");
//				        	sb.append(result.get(i).getTitle());
//				        	sb.append("</a></span></section>");
//				        }
//
//				        sb.append("</body></html>");
//
//				        wv.getSettings().setJavaScriptEnabled(true);
//
//				        JsObj jsObj = new JsObj();
//				        wv.addJavascriptInterface(jsObj, "Android");
//				        wv.setOnClickListener(new OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								// TODO 自動生成されたメソッド・スタブ
//								WebView wv = (WebView)v;
//								WebView.HitTestResult resultz = wv.getHitTestResult();
//								String url = resultz.getExtra();
//
//							}
//						});

//				        wv.loadData(sb.toString(), "text/html", "UTF-8");

						listCallback(http.getInputStream());
						http.disconnect();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			});
    	}
	}

	private void listCallback(InputStream inputStream) throws IOException{
		InputStream in = inputStream;
		byte b[] = new byte[65536];
		in.read(b);
		in.close();
		String res = new String(b);

		if (result != null && result.size() > 0)
			result.clear();

		xmlResultParser(res);

		nomula.Nico.ResultListAdapter adapter = new nomula.Nico.ResultListAdapter(Nico.this, R.layout.row2, result);
		ListView lv = (ListView)findViewById(R.id.listView2);
		lv.removeAllViewsInLayout();
		lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				RankingResults resp = result.get(position);

				Intent intent = new Intent(Nico.this, Detail.class);
				intent.putExtra("ID", resp.getID());
				intent.putExtra("Title", resp.getTitle());
				intent.putExtra("Description", resp.getDescription());
				intent.putExtra("Length", resp.getLength());
				intent.putExtra("MovieType", resp.getMovieType());
				intent.putExtra("Thumbnail", resp.getThumbnail());
				intent.putExtra("Tag", resp.getTagListAll());
				intent.putExtra("CommentCount", resp.getCommentCount());
				intent.putExtra("ViewCount", resp.getViewCount());
				intent.putExtra("MylistCount", resp.getMylistCount());
				intent.putExtra("UploadTime", resp.getUploadTime());
				intent.putExtra("SessionID", Nico.sessionID);
				startActivity(intent);
			}
		});
	}

	private void xmlResultParser(String str) {
    	str = str.substring(0, str.lastIndexOf("\n"));
    	if (result == null)
    		result = new ArrayList<RankingResults>();

//    	while (result.size() == 0){
			try {
		    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder db = dbf.newDocumentBuilder();
		    	InputSource is = new InputSource(new StringReader(str));
		    	Document doc = db.parse(is);
//		    	doc.getDocumentElement().normalize();
		    	NodeList list = doc.getElementsByTagName("video_info");
		    	RankingResults res = null;
		    	ArrayList<String> tag = null;

		    	for (int i = 0; i < list.getLength(); i++){
		    		Element element = (Element)list.item(i);
		    		Element videoList = (Element)element.getElementsByTagName("video").item(0);

		    		res = new RankingResults();
		    		res.setID(videoList.getElementsByTagName("id").item(0).getTextContent());
		    		res.setTitle(videoList.getElementsByTagName("title").item(0).getTextContent());
		    		res.setDescription(videoList.getElementsByTagName("description").item(0).getTextContent());
		    		res.setLength(videoList.getElementsByTagName("length_in_seconds").item(0).getTextContent());
		    		res.setMovieType(videoList.getElementsByTagName("movie_type").item(0).getTextContent());
		    		res.setThumbnail(videoList.getElementsByTagName("thumbnail_url").item(0).getTextContent());
		    		res.setViewCount(videoList.getElementsByTagName("view_counter").item(0).getTextContent());
		    		res.setMylistCount(videoList.getElementsByTagName("mylist_counter").item(0).getTextContent());
		    		res.setUploadTime(videoList.getElementsByTagName("upload_time").item(0).getTextContent());

		    		Element threadElement = (Element)element.getElementsByTagName("thread").item(0);
		    		res.setCommentCount(threadElement.getElementsByTagName("num_res").item(0).getTextContent());

		    		Element tagElement = (Element)element.getElementsByTagName("tags").item(0);
		    		NodeList tagList = (NodeList) tagElement.getElementsByTagName("tag_info");
		    		tag = new ArrayList<String>();
		    		for (int j = 0; j < tagList.getLength(); j ++){
		    			Element tags = (Element)tagList.item(j);
		    			tag.add(tags.getElementsByTagName("tag").item(0).getTextContent());
		    		}
		    		res.setTagList(tag);

		    		result.add(res);
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}
//    	}
    }

	private void xmlRankingParser(String str) {
    	str = str.substring(0, str.lastIndexOf("\n"));
		XmlPullParser xml = Xml.newPullParser();
		try {
			xml.setInput(new StringReader(str));

			String text = null;
			Ranking item = null;
			boolean isIdCheck = true;

			for (int eventType = xml.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xml.next()){
				switch (eventType){
					case XmlPullParser.TEXT:
						text =xml.getText();
						if (!text.contains("\n")){
							if (isIdCheck){
								item = new Ranking();
								item.setTitleId(text);
								isIdCheck = false;
							} else{
								item.setTitle(text);
								list.add(item);
								isIdCheck = true;
							}
						}
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();

        switch(v.getId()) {
        case R.id.layout_first:
        case R.id.layout_second:
        case R.id.layout_third:
        case R.id.layout_fourth:
            switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouch = event.getRawX();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!isFlip) {
                    if(x - firstTouch > 50) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.move_in_left));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.move_out_right));
                        viewFlipper.showNext();
                    }
                    else if(firstTouch - x > 50) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.move_in_right));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.move_out_left));
                        viewFlipper.showPrevious();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFlip = false;
                break;
            }
        }

        return false;
    }

//    public class JsObj {
//    	private Handler handler;
//    	private Activity activity;
//
//    	public JsObj(){
//    	}
//    	public JsObj(Activity activity){
//    		this.activity = activity;
//    		handler = new Handler();
//    	}
//
//    	public void getTitle(String title){
//    		Toast.makeText(Nico.this, "ok", Toast.LENGTH_LONG).show();
//    	}
//    }
}

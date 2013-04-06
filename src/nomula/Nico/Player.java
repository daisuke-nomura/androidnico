package nomula.Nico;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nomula.Nico.R;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Player extends Activity implements AnimationListener {
	ArrayList<Comment> commentFlow = new ArrayList<Comment>();
	ArrayList<Comment> comment = new ArrayList<Comment>();
	ArrayList<CommentData> ue = new ArrayList<CommentData>();
	ArrayList<CommentData> shita = new ArrayList<CommentData>();
	int seed, now, interval = 25, j = 0, ueDel = 0, shitaDel = 0, comDel = 0;
	Timer timer = null;
	Handler handler = new Handler();
	String sessionID = null, threadID = null, messageServer = null, videoLength = null, videoServer = null, post = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        final TextView tv = new TextView(getApplicationContext());
        tv.setText("パラメータ");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.GREEN);
        tv.setShadowLayer(1, 1, 1, Color.BLACK);

        Intent intent = getIntent();
        sessionID = intent.getStringExtra("SessionID");

        final TextView title = new TextView(getApplicationContext());
        title.setText(intent.getStringExtra("Title"));
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(Color.BLACK);
        title.setTextSize(22);
        title.setPadding(0, 0, 0, 50);

        final TextView about = new TextView(getApplicationContext());
        String aboutText = intent.getStringExtra("Description");

        if (aboutText.length() > 25)
        	aboutText = aboutText.substring(0, 25);

        about.setText(aboutText);
        about.setGravity(Gravity.CENTER);
        about.setTextSize(18);
        about.setPadding(0, 50, 0, 0);

        FrameLayout f = (FrameLayout)findViewById(R.id.frameLayout1);
        f.addView(tv);
        f.addView(title);
        f.addView(about);

		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);
		CookieManager.getInstance().removeExpiredCookie();
		DefaultHttpClient httpClient;

        String res = null, status = null, type = null, ckey = null, enableLow = null, enableOrg = null;
		try {
			CookieManager cookie = CookieManager.getInstance();
			//String cookieStr = cookie.getCookie("http://i.nicovideo.jp/v2/video.play?sid=" + sessionID + "&device=iPhone2%2C1&network=wifi&vid=" + intent.getStringExtra("ID") + "&v=2%2E40");
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.getParams().setParameter("http.connection.timeout", 10000);
			httpClient.getParams().setParameter("http.socket.timeout", 10000);
			HttpGet httpget = new HttpGet("http://i.nicovideo.jp/v2/video.play?sid=" + sessionID + "&device=iPhone2%2C1&network=wifi&vid=" + intent.getStringExtra("ID") + "&v=2%2E40");
//			httpget.setHeader("Accept", "*/*");
//			httpget.setHeader("Accept-Encoding", "gzip, deflate");
//			httpget.setHeader("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
			HttpResponse response = httpClient.execute(httpget);
			InputStream in = response.getEntity().getContent();
//			URL uri = new URL("http://i.nicovideo.jp/v2/video.play?sid=" + sessionID + "&device=iPhone2%2C1&network=wifi&vid=" + intent.getStringExtra("ID") + "&v=2%2E40");
//			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
//			http.setRequestMethod("GET");
//			http.setRequestProperty("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
//			http.connect();
//
//			InputStream in = http.getInputStream();
			byte b[] = new byte[65536];
			in.read(b);
			in.close();
			res = new String(b);
//			http.disconnect();

			// HttpClientで得たCookieの情報をWebViewでも利用できるようにする
			CookieStore cookieStr = httpClient.getCookieStore();
			Cookie cookie1 = null;
			if ( cookieStr != null ) {
				List<Cookie> cookies = cookieStr.getCookies();
				if (!cookies.isEmpty()) {
					for (int i = 0; i < cookies.size(); i++) {
						cookie1 = cookies.get(i);
					}
				}
				if (cookie1 != null) {
					String cookieString = cookie1.getName() + "=" + cookie1.getValue() + "; domain=" + cookie1.getDomain();
					CookieManager.getInstance().setCookie( "nicovideo.jp", cookieString);
					CookieSyncManager.getInstance().sync();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (res != null && res.contains("ok")){
			try {
		    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(res));
	    		Document doc = db.parse(is);
	    		NodeList node = doc.getElementsByTagName("nicolive_videoplay_response");
	    		Element element = (Element)node.item(0);
	    		type = element.getElementsByTagName("type").item(0).getTextContent();
	    		enableLow = element.getElementsByTagName("enable_low").item(0).getTextContent();
	    		enableOrg = element.getElementsByTagName("enable_org").item(0).getTextContent();

	    		Element getflv = (Element)element.getElementsByTagName("getflv").item(0);
	    		ckey = getflv.getElementsByTagName("ckey").item(0).getTextContent();
			} catch(Exception e){
				e.printStackTrace();
			}
		}



		if (ckey != null){
			try {
				// HttpClientの準備
				final HttpResponse hs = null;
				httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
				httpClient.getParams().setParameter("http.connection.timeout", 5000);
				httpClient.getParams().setParameter("http.socket.timeout", 3000);


				HttpPost httppost = new HttpPost("https://secure.nicovideo.jp/secure/login?site=niconico");
//				httppost.setHeader("Accept", "*/*");
//				httppost.setHeader("Accept-Encoding", "gzip, deflate");
//				httppost.setHeader("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//				httpClient.setRedirectHandler(new RedirectHandler() {
//
//					@Override
//					public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
//						return true;
//					}
//
//					@Override
//					public URI getLocationURI(HttpResponse response, HttpContext context)
//							throws ProtocolException {
//						Header[] h = response.getHeaders("Location");
//						String location = h[0].getValue();
//						return URI.create(location);
//					}
//				});
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
				nameValuePair.add(new BasicNameValuePair("mail", sp.getString("mail", "")));
				nameValuePair.add(new BasicNameValuePair("password", sp.getString("pass", "")));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				HttpResponse response = httpClient.execute(httppost);
				InputStream in = response.getEntity().getContent();
				in.close();

				// HttpClientで得たCookieの情報をWebViewでも利用できるようにする
				CookieStore cookieStr = httpClient.getCookieStore();
				Cookie cookie = null;
				if ( cookieStr != null ) {
					List<Cookie> cookies = cookieStr.getCookies();
					if (!cookies.isEmpty()) {
						for (int i = 0; i < cookies.size(); i++) {
							cookie = cookies.get(i);
						}
					}
					if (cookie != null) {
						String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
						CookieManager.getInstance().setCookie( "nicovideo.jp", cookieString);
						CookieSyncManager.getInstance().sync();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		try{
			CookieManager cookie = CookieManager.getInstance();
			String cookieStr = cookie.getCookie("nicovideo.jp");

			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.getParams().setParameter("http.connection.timeout", 10000);
			httpClient.getParams().setParameter("http.socket.timeout", 10000);
			HttpGet httppost = new HttpGet("http://www.nicovideo.jp/");
//			httppost.setHeader("Accept", "*/*");
//			httppost.setHeader("Accept-Encoding", "gzip, deflate");
			httppost.setHeader("Cookie", cookieStr);
//			httppost.setHeader("Referer", "file:///Applications/Install/461C1519-5E65-4F71-8E2B-121AFC358885/Install/");
//			httppost.setHeader("User-Agent", "AppleCoreMedia/1.0.0.8C148a (iPhone; U; CPU OS 4_2_1 like Mac OS X; ja_jp)");
				HttpResponse response = httpClient.execute(httppost);
			InputStream in = response.getEntity().getContent();
			in.close();

			// HttpClientで得たCookieの情報をWebViewでも利用できるようにする
			CookieStore cookieStr1 = httpClient.getCookieStore();
			Cookie cookie1 = null;
			if ( cookieStr1 != null ) {
				List<Cookie> cookies = cookieStr1.getCookies();
				if (!cookies.isEmpty()) {
					for (int i = 0; i < cookies.size(); i++) {
						cookie1 = cookies.get(i);
					}
				}
				if (cookie1 != null) {
					String cookieString = cookie1.getName() + "=" + cookie1.getValue() + "; domain=" + cookie1.getDomain();
					CookieManager.getInstance().setCookie( "nicovideo.jp", cookieString);
					CookieSyncManager.getInstance().sync();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



			try {
				CookieManager cookie = CookieManager.getInstance();
				String cookieStr = cookie.getCookie("nicovideo.jp");

				httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
				httpClient.getParams().setParameter("http.connection.timeout", 10000);
				httpClient.getParams().setParameter("http.socket.timeout", 10000);
				HttpPost httppost = new HttpPost("http://flapi.nicovideo.jp/api/getflv");
				httppost.setHeader("Accept", "*/*");
//				httppost.setHeader("Accept-Encoding", "gzip, deflate");
				httppost.setHeader("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httppost.setHeader("Cookie", cookieStr);
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
				nameValuePair.add(new BasicNameValuePair("eco", enableOrg));
				nameValuePair.add(new BasicNameValuePair("device", "iphone4"));
				nameValuePair.add(new BasicNameValuePair("v", intent.getStringExtra("ID")));
				nameValuePair.add(new BasicNameValuePair("ckey", ckey));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				HttpResponse response = httpClient.execute(httppost);
				InputStream in = response.getEntity().getContent();
//				in.close();

//				URL uri = new URL("http://flapi.nicovideo.jp/api/getflv");
//
//
//				HttpURLConnection http = (HttpURLConnection)uri.openConnection();
//				http.setRequestMethod("POST");
////				http.setRequestProperty("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
//				http.setRequestProperty("Cookie", cookieStr);
//				http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//				http.setDoOutput(true);
//
//				OutputStream os = http.getOutputStream();
//				PrintStream ps = new PrintStream(os);
//				String data = "eco=" + enableOrg + "&device=iphone4&v=" + intent.getStringExtra("ID") + "&ckey=" + ckey;
//				data = data.replace(".", "%2E");
//				ps.print(data);
//				ps.close();
//				os.close();
//
//				InputStream in = http.getInputStream();
				byte b[] = new byte[1024];
				in.read(b);
				in.close();
				res = new String(b);
//				http.disconnect();

				// HttpClientで得たCookieの情報をWebViewでも利用できるようにする
				CookieStore cookieStr1 = httpClient.getCookieStore();
				Cookie cookie1 = null;
				if ( cookieStr1 != null ) {
					List<Cookie> cookies = cookieStr1.getCookies();
					if (!cookies.isEmpty()) {
						for (int i = 0; i < cookies.size(); i++) {
							cookie1 = cookies.get(i);
						}
					}
					if (cookie1 != null) {
						String cookieString = cookie1.getName() + "=" + cookie1.getValue() + "; domain=" + cookie1.getDomain();
						CookieManager.getInstance().setCookie( "nicovideo.jp", cookieString);
						CookieSyncManager.getInstance().sync();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String[] data1 = res.split("&");
		for (int i = 0; i < data1.length; i++){
			String[] buff = data1[i].split("=");

			if (buff[0].equals("thread_id")){
				threadID = buff[1];
				post = "<thread thread=\"" + buff[1] + "\" res_from=\"-200\" version=\"20061206\" />";
			} else if (buff[0].equals("ms")){
				messageServer = URLDecoder.decode(buff[1]);
			} else if (buff[0].equals("l")){
				videoLength = buff[1];
			} else if (buff[0].equals("url")){
				videoServer = URLDecoder.decode(buff[1]);
			}
		}

		try {
			CookieManager cookie = CookieManager.getInstance();
			String cookieStr = cookie.getCookie("nicovideo.jp");

//			httpClient = new DefaultHttpClient();
//			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
//			httpClient.getParams().setParameter("http.connection.timeout", 5000);
//			httpClient.getParams().setParameter("http.socket.timeout", 3000);
//			HttpPost httppost = new HttpPost(messageServer);
//			httppost.setHeader("Accept", "*/*");
//			httppost.setHeader("Accept-Encoding", "gzip, deflate");
//			httppost.setHeader("User-Agent", "NicoPlayer/2.40 CFNetwork/485.12.7 Darwin/10.4.0");
//			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//			httppost.setHeader("Cookie", cookieStr);
//			List<? extends NameValuePair> nameValuePair = new ArrayList<String>(1);
//			nameValuePair.add(post);
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePair));
//			HttpResponse response = httpClient.execute(httppost);
//			InputStream in = response.getEntity().getContent();


//			CookieManager cookie = CookieManager.getInstance();
//			String cookieStr = cookie.getCookie("http://flapi.nicovideo.jp/api/getflv");
//
			URL uri = new URL(messageServer);
			HttpURLConnection http = (HttpURLConnection)uri.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Cookie", cookieStr);
			http.setDoOutput(true);

			OutputStream os = http.getOutputStream();
			PrintStream ps = new PrintStream(os);
			ps.print(post);
			ps.close();
			os.close();

			InputStream in = http.getInputStream();
			byte b[] = new byte[65536];
			in.read(b);
			in.close();
			http.disconnect();
			res = new String(b);
		} catch(Exception e){
			e.printStackTrace();
		}

		try {
			CookieManager cookie = CookieManager.getInstance();
			String cookieStr = cookie.getCookie("nicovideo.jp");

			// HttpClientの準備
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.getParams().setParameter("http.connection.timeout", 10000);
			httpClient.getParams().setParameter("http.socket.timeout", 10000);
			HttpGet httppost = new HttpGet(videoServer);
			httppost.setHeader("Accept", "*/*");
//			httppost.setHeader("Accept-Encoding", "gzip, deflate");
			httppost.setHeader("Cookie", cookieStr);
//			httppost.setHeader("User-Agent", "AppleCoreMedia/1.0.0.8C148a (iPhone; U; CPU OS 4_2_1 like Mac OS X; ja_jp)");
				HttpResponse response = httpClient.execute(httppost);


				String filePath = Environment.getExternalStorageDirectory() + "/smile.mp4";
		        File file = new File(filePath);
		        file.getParentFile().mkdir();

		        FileOutputStream fos = new FileOutputStream(file, false);

				InputStream in = response.getEntity().getContent();
				byte bw[] = new byte[1024];

				int i = 0;
	            for (int l = 0; (i = in.read(bw)) >0; l+=i) {
	                fos.write(bw, 0, i);
	            }
	            in.close();
			} catch (Exception e) {
				// FIXME
			}


		XmlParse(res);

        FrameLayout b = (FrameLayout)findViewById(R.id.linearLayout);
        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
	            VideoView video = (VideoView)findViewById(R.id.videoView);
	            if (!video.isPlaying()){

	                FrameLayout f = (FrameLayout)findViewById(R.id.frameLayout1);
	                f.removeView(title);
	                f.removeView(about);

	                video.setVideoPath("/sdcard/smile.mp4");
	                video.start();
	            }

		        timer = new Timer();
		        timer.schedule(new TimerTask() {
					@Override
					public void run() {
						now += interval;

						if (commentFlow.size() > 0){
							for (int i = 0; i < commentFlow.size(); i++){
								commentFlow.get(i).ShowedTime += interval;
							}
						}


						if (ue.size() > 0){
							for (int i = 0; i < ue.size(); i++){
								ue.get(i).ShowedTime += interval;
							}

							for (int i = 0; i < ue.size(); i++){
								if (ue.get(i).ShowedTime == 4000){
									ueDel++;
									ue.remove(0);
								}
							}
						}

						if (shita.size() > 0){
							for (int i = 0; i < shita.size(); i++){
								shita.get(i).ShowedTime += interval;
							}

							for (int i = 0; i < shita.size(); i++){
								if (shita.get(i).ShowedTime == 4000){
									shitaDel++;
									shita.remove(0);
								}
							}
						}

						handler.post(new Runnable() {
							@Override
							public void run() {
				            	tv.setText(String.valueOf("中" + commentFlow.size()) + "　上　" + String.valueOf(ue.size()) + "　下　" +String.valueOf(shita.size()) + "　総　" + String.valueOf(j) + "　タイマー　" +String.valueOf(now));

								while (comment.get(j).Vpos <= now && j < comment.size()){
									CommentFlow(comment.get(j));
									j++;
								}

								for (int i = 0; i < ueDel+shitaDel; i++){
									FrameLayout layout= (FrameLayout)findViewById(R.id.linearLayout);
									layout.removeViewAt(0);
								}
								ueDel = 0;
								shitaDel = 0;
							}
						});
					}
				}, 0, interval);
			}
		});
    }

    private void XmlParse(String str) {
		// TODO 自動生成されたメソッド・スタブ

		XmlPullParser xml = Xml.newPullParser();
		try {
			xml.setInput(new StringReader(str));

			String text, mail = null;
			int vpos  = 0;

			for (int eventType = xml.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xml.next()){
				switch (eventType){
					case XmlPullParser.START_TAG:
							for (int i = 0; i < xml.getAttributeCount(); i++){
								if (xml.getAttributeName(i) == "vpos"){
									vpos = Integer.parseInt(xml.getAttributeValue(i));
								}
								else if (xml.getAttributeName(i) == "mail"){
									mail = xml.getAttributeValue(i);
								}
							}
						break;

					case XmlPullParser.TEXT:
						text =xml.getText();
						Comment c = new Comment();
						c.Text = text;
						c.Vpos = vpos * 10;
						c.setMail(mail);
						comment.add(c);
						break;
				}
			}
		} catch (XmlPullParserException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		QuickSort(0, comment.size()-1);
	}

	private void CommentFlow(Comment co){
		double top = 0;
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();

		FrameLayout layout = (FrameLayout)findViewById(R.id.linearLayout);

		TextView text = new TextView(getApplicationContext());
		text.setText(co.Text.trim());
		text.setTextSize(co.FontSize - 20);
		text.setId(0);
		text.setTextColor(co.FontColor);
		text.setShadowLayer(1, 1, 1, Color.BLACK);

		double textWidth = co.FontSize * text.length();

		if (co.Point.equals("n"))
			text.setLayoutParams(new LayoutParams((int)textWidth, co.FontSize));

		layout.addView(text);

		if (co.Point.equals("n")){
			double speed = (width + textWidth) / 4;//毎秒動くピクセル数
			top = GetCommentLane(textWidth, speed, width, height, co.FontSize);
			double left = width;
			double toValue = 0 - textWidth;

			Comment c = new Comment();
			c.Text = co.Text.trim();
			c.FontSize = co.FontSize;
			c.Left = left;
			c.Speed = speed;
			c.Top = top;
			c.ToValue = toValue;
			c.Width = (int)textWidth;
			commentFlow.add(c);

			textWidth *= -1;
			TranslateAnimation translation = new TranslateAnimation((int)width, (int)textWidth, (int)top, (int)top);
			translation.setInterpolator(new LinearInterpolator());
			translation.setAnimationListener(this);
			translation.setDuration(4000);
			translation.setFillAfter(true);
			text.startAnimation(translation);

		} else{
			CommentData c = new CommentData();
			c.Text = co.Text.trim();
			c.FontSize = co.FontSize;
			c.Width = (int)textWidth;

			if (co.Point.equals("u")){
				top = GetUeShita(co.FontSize, true);
				c.Top = (int)top;
				ue.add(c);
			} else{
				top = GetUeShita(co.FontSize, false);
				c.Top = (int)top;
				top = height - co.FontSize - top;//topは
				shita.add(c);
			}

			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setPadding(0, (int)top, 0, 0);
		}
	}

    private int GetCommentLane(double width, double speed, double canvasWidth, double canvasHeight, int fontSize){
		int top, count = commentFlow.size();
		int canvasHeightMinusFontsize = (int)canvasHeight - fontSize;
		double next = canvasWidth / speed;//配置しようとしているテキスト

        for (top = 0; count > 0 && top < canvasHeightMinusFontsize; top += 10){//10pxずつチェックしていく
            CommentDataFlow cd = null;
            int topAndFontsize = top + fontSize;

            for (Comment obj : commentFlow)
            {
                if (topAndFontsize - obj.Top < 0)
                    continue;

                if (obj.Top + obj.FontSize > top)
                {
                    if (cd == null){
                    	cd = new CommentDataFlow();
                    	cd.Width = obj.Width;
                    	cd.Top = (int)obj.Top;
                    	cd.Text = obj.Text;
                    	cd.Speed = obj.Speed;
                    	cd.ShowedTime = obj.ShowedTime;
                        cd.FontSize = obj.FontSize;
                    }
                    else if (cd.ShowedTime >= obj.ShowedTime){//showedTimeが一番小さいものを取り出す
                    	cd.Width = obj.Width;
                    	cd.Top = (int)obj.Top;
                    	cd.Text = obj.Text;
                    	cd.Speed = obj.Speed;
                    	cd.ShowedTime = obj.ShowedTime;
                        cd.FontSize = obj.FontSize;
                    }
                }
            }

            //表示しようとするコメントが左端に到達するまでどれくらいの時間がかかるかを計算し、
            //表示されているコメントが表示し終わるよりも早ければ配置不可能、
            //表示されているコメントが表示し終わってから左端に来るなら配置可能
            //同じレーン且つ同じレーンの中で一番最後の要素に対して衝突判定を行う

            if (cd == null)
                return top;

            double prev = ((cd.Width + canvasWidth) - cd.Speed * (cd.ShowedTime / 1000)) / cd.Speed;//同じレーンの一番最後のテキスト

            //配置する要素が左端に来る時間 - 前の要素が消える時間
            if (next - prev > 0)//衝突しない
                return top;
        }

        if (count > 0){
            top = new Random(seed+=10).nextInt(canvasHeightMinusFontsize);//弾幕
        }

    	return top;
	}

    private int GetUeShita(int fontSize, boolean pos){
    	int top, count = 0;
    	ArrayList<CommentData> target = null;

    	if (pos){
    		count = ue.size();
    		target = ue;
    	}
    	else {
    		count = shita.size();
    		target = shita;
    	}

    	for (top = 0; count > 0; top+=10){
    		boolean flag = true;

    		for (CommentData obj : target){
    			if (top == obj.Top || top < obj.Top + obj.FontSize){
    				flag = false;
    				break;
    			}
    		}

    		if (flag)
    			return top;
    	}

    	return top;
    }


	@Override
	public void onAnimationEnd(Animation animation) {
		commentFlow.remove(0);

			handler.post(new Runnable() {
				@Override
				public void run() {
					FrameLayout layout = (FrameLayout)findViewById(R.id.linearLayout);
					layout.removeViewAt(0);
				}
			});
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO 自動生成されたメソッド・スタブ
	}

	private void QuickSort(int left, int right) {
		// TODO 自動生成されたメソッド・スタブ
		if (left <= right){
			int p = comment.get((left+right) / 2).Vpos;
			int l = left;
			int r = right;

			while (l <= r){
				while (comment.get(l).Vpos < p)
					l++;
				while (comment.get(r).Vpos > p)
					r--;

				if (l <= r){
					Comment buff = comment.get(l);
					comment.set(l, comment.get(r));
					comment.set(r, buff);
					l++;
					r--;
				}
			}

			QuickSort(left, r);
			QuickSort(l, right);
		}
	}
}
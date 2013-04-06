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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Clear extends Activity implements AnimationListener {
	ArrayList<Comment> commentFlow = new ArrayList<Comment>();
	ArrayList<Comment> comment = new ArrayList<Comment>();
	ArrayList<CommentData> ue = new ArrayList<CommentData>();
	ArrayList<CommentData> shita = new ArrayList<CommentData>();
	int seed, now, interval = 25, j = 0, ueDel = 0, shitaDel = 0, comDel = 0;
	Timer timer = null;
	Handler handler = new Handler();
	String sessionID = null, threadID = null, messageServer = null, videoLength = null, videoServer = null, post = null;
	boolean prev;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clear);

        final TextView tv = new TextView(getApplicationContext());
        tv.setText("パラメータ");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.GREEN);
        tv.setShadowLayer(1, 1, 1, Color.BLACK);

        Button bb = (Button)findViewById(R.id.button1);
        bb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final int FLASH_PLAY = 0;
				final int FLASH_PAUSE = 1;
				final int FLASH_STOP = 2;
				final int FLASH_MOVE = 3;
				final int FLASH_RESUME = 4;
				Flash.ping = true;

				if (prev){
					Flash.content = FLASH_MOVE;
					Flash.position = 50;
					prev = false;
				} else{
					Flash.content = FLASH_PLAY;
					prev = true;
				}
			}
		});


		String res = ""
        XmlParse(res);

        FrameLayout b = (FrameLayout)findViewById(R.id.linearLayout12);
        b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
	                FrameLayout f = (FrameLayout)findViewById(R.id.frameLayout12);

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
									FrameLayout layout= (FrameLayout)findViewById(R.id.linearLayout12);
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

//        Button b = (Button)findViewById(R.id.button1);
//        b.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO 自動生成されたメソッド・スタブ
//				TextView tv = (TextView)findViewById(R.id.textView1);
//				Comment co = new Comment();
//				co.Text = "hello world";
//				co.FontSize = 30;
//				co.Point = "n";
//				co.FontColor = Color.WHITE;
//				CommentFlow(co);
//			}
//		});
	}

	private void CommentFlow(Comment co){
		double top = 0;
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();

		FrameLayout layout = (FrameLayout)findViewById(R.id.linearLayout12);

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
					FrameLayout layout = (FrameLayout)findViewById(R.id.linearLayout12);
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
package nomula.Nico;

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Flash extends Activity {
	public static boolean ping = false;
	public static int content = 0, position = 0;
	Timer timer = null;
	int interval = 100;
	WebView wv = null;
	String ipAddr;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        try {
//			CookieSyncManager.createInstance(this);
//			CookieSyncManager.getInstance().startSync();
//			CookieManager.getInstance().setAcceptCookie(true);
//			CookieManager.getInstance().removeExpiredCookie();
//
//			CookieManager cookie = CookieManager.getInstance();
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
//			HttpGet httpget = new HttpGet("http://192.168.1.16/res.php");
//			HttpResponse response = httpClient.execute(httpget);
//			InputStream in = response.getEntity().getContent();
//			byte b[] = new byte[1024];
//			in.read(b);
//			in.close();
//			String res = new String(b);
//
//			CookieStore cookieStr = httpClient.getCookieStore();
//			Cookie cookie1 = null;
//			if ( cookieStr != null ) {
//				List<Cookie> cookies = cookieStr.getCookies();
//				if (!cookies.isEmpty()) {
//					for (int i = 0; i < cookies.size(); i++) {
//						cookie1 = cookies.get(i);
//					}
//				}
//				if (cookie1 != null) {
//					String cookieString = cookie1.getName() + "=" + cookie1.getValue() + "; domain=" + cookie1.getDomain();
//					CookieManager.getInstance().setCookie( "192.168.1.16", cookieString);
//					CookieSyncManager.getInstance().sync();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


        wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setPluginsEnabled(true);
        wv.setVerticalScrollBarEnabled(false);
        wv.addJavascriptInterface(new Hoge(this), "android");
        wv.setClickable(true);
//        wv.setWebChromeClient(new WebChromeClient(){
//        	@Override
//        	public Boolean onJsAlert(WebView view, String url, String message, JsResult result) {
////                AlertDialog.Builder adb=new AlertDialog.Builder(HistreeView.this);
////                adb.setMessage(message);
////                adb.setPositiveButton(R.string.OK, null);
////                adb.show();
////
////                result.confirm();
//                return true;
//        	}
//        });

//        final class ChromeClient extends WebChromeClient {
//            //アラートイベントの処理
//            @Override
//            public boolean onJsAlert(WebView view,String url,String message,JsResult result) {
//                android.util.Log.e("",message);
//                result.confirm();
//                return true;
//            }
//        }
        setContentView(wv);
        wv.loadUrl("");

//		LinearLayout ll = new LinearLayout(this);
//		ll.layout(0, 0, 800, 480);
//	    setContentView(ll);
//
//		TextView tv = new TextView(this);
//		tv.setText("hello flash");
//		tv.setTextColor(Color.WHITE);
//		ll.addView(tv);

//      WebView wv = new WebView(this);
//      wv.getSettings().setPluginsEnabled(true);
//      wv.getSettings().setJavaScriptEnabled(true);
//      wv.setVerticalScrollBarEnabled(false);
//      wv.addJavascriptInterface(new Hoge(this), "android");
//      setContentView(wv);
//      wv.loadUrl("file:///android_asset/index.html");

        timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (ping){
					ping = false;

					final int FLASH_PLAY = 0;
					final int FLASH_PAUSE = 1;
					final int FLASH_STOP = 2;
					final int FLASH_MOVE = 3;
					final int FLASH_RESUME = 4;
					switch (content){
						case FLASH_PLAY:
							flashPlay(ipAddr);
							break;
						case FLASH_PAUSE:
							flashPause();
							break;
						case FLASH_STOP:
							flashStop();
							break;
						case FLASH_RESUME:
							flashResume();
							break;
						case FLASH_MOVE:
							flashMove(position);
							break;
					}
				}
			}

			private void flashPlay(String ipAddr) {
				wv.loadUrl("javascript:videoPlayAS(" + ipAddr + ")");
			}

			private void flashPause() {
				wv.loadUrl("javascript:videoPauseAS();");
			}

			private void flashStop() {
				wv.loadUrl("javascript:stop()");
			}

			private void flashResume() {
				wv.loadUrl("javascript:videoResumeAS()");
			}

			private void flashMove(int position) {
				wv.loadUrl("javascript:videoMoveAS(" + position +")");
			}

		}, 0, interval);

      Intent intent = new Intent(Flash.this, Clear.class);
      startActivity(intent);

    }

    public class Hoge {
        private Context con;

        public Hoge(Context con) {
          this.con = con;
        }

        public void appearToast(String message) {
            Toast.makeText(con, message, Toast.LENGTH_LONG).show();
        }
    }
}
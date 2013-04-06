package nomula.Nico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.MotionEvent;
import android.view.View;

public class Test extends Activity implements View.OnTouchListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlist);

        ArrayList<Map<String, Object>> data;
        ListTemplateAdapter adapter = null;        //独自のアダプタを作成

        String[] from_template = {"url","viewImage"};
        int[] to_template = {R.id.TextUrl, R.id.ImageThumb};

        //画面のリストビュー
        ListView list_group = (ListView)findViewById(R.id.listGroup);

        //テンプレートのリスト取得
        data = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        //データの設定（仮・データ件数毎の設定）
        map = new HashMap<String, Object>();
        data.add(map);

        adapter = new ListTemplateAdapter(this, data, R.layout.column, from_template, to_template);

        if(data != null && data.size() > 0){
            adapter.setListData(data);    //テンプレートにデータ内容を保持

            list_group.setFocusable(false);
            list_group.setAdapter(adapter);
            list_group.setScrollingCacheEnabled(false);
            list_group.setTextFilterEnabled(true);

            list_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //リスト選択時の処理

                    ImageCache.clearCache();    //キャッシュのクリア

                    //終了処理
                }
            });

            list_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //リスト選択時の処理

                    ImageCache.clearCache();    //キャッシュのクリア

                    //終了処理
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
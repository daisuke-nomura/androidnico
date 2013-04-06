package nomula.Nico;

import java.util.List;
import nomula.Nico.R;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TagListAdapter extends ArrayAdapter<String>{
    private List<String> items;
    private LayoutInflater inflater;

    //コンストラクタ
    public TagListAdapter(Context context, int resourceId, List<String> items) {
        super(context, resourceId, items);
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            //1行分layoutからViewの塊を生成
            v = inflater.inflate(R.layout.row, null);
        }
        //itemsからデータ
        //vから画面にくっついているViewを取り出して値をマッピングする
//        String str = (String)items.get(position);
        String str = items.get(position);
        //final String fHoge = str;
        TextView hogeText = (TextView)v.findViewById(R.id.row_text);
//        hogeText.setText(str);
        hogeText.setText(str);
        hogeText.setTextColor(Color.parseColor("#408BEF"));
//        hogeText.setGravity(Gravity.LEFT);
        hogeText.setTextSize(16);
        hogeText.setGravity(Gravity.CENTER_VERTICAL);


        return v;
    }
}
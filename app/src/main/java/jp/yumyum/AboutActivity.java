package jp.yumyum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        String supportLink = "Produced by <a href=http://twitter.com/yu_yum>yu_yum</a>";
        CharSequence cs = Html.fromHtml(supportLink);
        TextView mTextView = (TextView) findViewById(R.id.supportLink);
        MovementMethod mm = LinkMovementMethod.getInstance();
        mTextView.setMovementMethod(mm);
        mTextView.setText(cs);

        Button helpBtn = (Button) findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        // インテントの生成
        Intent intent = new Intent(this, HelpActivity.class);

        // アクティビティの呼び出し
        startActivity(intent);
        finish();
    }

}

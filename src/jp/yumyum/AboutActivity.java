package jp.yumyum;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		String supportLink = "Produced by <a href=http://twitter.com/yu_yum>yu_yum</a>";
		CharSequence cs = Html.fromHtml(supportLink);
		TextView mTextView = (TextView)findViewById(R.id.supportLink);
		MovementMethod mm = LinkMovementMethod.getInstance();
		mTextView.setMovementMethod(mm);
		mTextView.setText(cs);
	}

}

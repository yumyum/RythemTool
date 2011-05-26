package jp.yumyum;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);
		super.onCreate(savedInstanceState);
	}

}

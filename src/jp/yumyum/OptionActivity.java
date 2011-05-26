package jp.yumyum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OptionActivity extends Activity implements View.OnClickListener {
	private int currentBPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.option);
		Button okBtn = (Button) findViewById(R.id.opOKbtn);
		okBtn.setOnClickListener(this);
		Intent intent = getIntent();
		currentBPM = intent.getIntExtra("CurrentBPM", 120);
		EditText et = (EditText) findViewById(R.id.bpmEditText);
		et.setText(String.valueOf(currentBPM));
		et.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// エンターキーのup時ソフトウェアキーボードを隠す
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					inputMethodManager.hideSoftInputFromWindow(
							v.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
	}

	public void onClick(View v) {
		if (v.getId() == R.id.opOKbtn) {
			// OKの戻り値の指定
			EditText et = (EditText) findViewById(R.id.bpmEditText);
			SpannableStringBuilder sb = (SpannableStringBuilder) et.getText();
			int bpm;
			try {
				bpm = Integer.parseInt(sb.toString());
				if (bpm <= 0 || bpm > 999)
					throw (new NumberFormatException());
			} catch (NumberFormatException e) {
				Toast.makeText(this, R.string.tempo_caution, Toast.LENGTH_LONG)
						.show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("bpm", bpm);
			setResult(Activity.RESULT_OK, intent);

			// アクティビティの終了
			finish();
		}
	}
}

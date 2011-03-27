package jp.yumyum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OptionActivity extends Activity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);
		Button okBtn = (Button) findViewById(R.id.opOKbtn);
		okBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.opOKbtn) {
			// OKの戻り値の指定
			EditText et = (EditText) findViewById(R.id.bpmEditText);
			SpannableStringBuilder sb = (SpannableStringBuilder) et.getText();
			int bpm = Integer.parseInt(sb.toString());
			Intent intent = new Intent();
			intent.putExtra("bpm", bpm);
			setResult(Activity.RESULT_OK, intent);

			// アクティビティの終了
			finish();
		}
	}
}

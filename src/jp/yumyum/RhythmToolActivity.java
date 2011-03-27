package jp.yumyum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RhythmToolActivity extends Activity {
	ShowArea mShowArea;

	static final int REQUEST_OPTION = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// ���C����LinearLayout���擾
		LinearLayout linear = (LinearLayout) findViewById(R.id.Linear01);

		// ���C�A�E�g�p�����[�^�쐬
		LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, 1);

		// �\���G���A�쐬�ALinearLayout�֓o�^
		// FrameLayout���g�p����
		FrameLayout fl = new FrameLayout(this);
		fl.setLayoutParams(lParam);
		ValueView vv = new ValueView(this);
		mShowArea = new ShowArea(this, vv);
		mShowArea.setLayoutParams(lParam);
		// FrameLayout��ShowArea��ValueView��ǉ�
		fl.addView(mShowArea);
		fl.addView(vv);
		// LinearLayout��FrameLayout��ǉ�
		linear.addView(fl);

		// �^�b�v�G���A�쐬�ALinearLayout�֓o�^
		TapArea tmpT = new TapArea(this, mShowArea);
		tmpT.setLayoutParams(lParam);
		linear.addView(tmpT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.mReset:
			mShowArea.reset();
			return true;
		case R.id.mOption:
			// �C���e���g�̐���
			Intent intent = new Intent(this, OptionActivity.class);

			// �A�N�e�B�r�e�B�̌Ăяo��
			startActivityForResult(intent, REQUEST_OPTION);
			return true;
		}
		return false;
	}

	// �A�N�e�B�r�e�B�Ăяo�����ʂ̎擾
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == REQUEST_OPTION && resultCode == RESULT_OK) {
			// �C���e���g����̃p�����[�^�擾
			int bpm = -1;
			Bundle extras = intent.getExtras();
			if (extras != null)
				bpm = extras.getInt("bpm");

			// BPM�̐ݒ�
			mShowArea.setTargetBpm(bpm);
		}
	}
}

package jp.yumyum;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RhythmToolActivity extends Activity {
	private ShowArea mShowArea;
	private TapArea mTapArea;
	private LinearLayout rootLayout;
	private SharedPreferences sharedPreferences;

	static final int REQUEST_OPTION = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.pref, true);
		setContentView(R.layout.main);
		// メインのLinearLayoutを取得
		if (rootLayout == null) {
			rootLayout = (LinearLayout) findViewById(R.id.Linear01);

			// レイアウトパラメータ作成
			LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1);

			// 表示エリア作成、LinearLayoutへ登録
			// FrameLayoutを使用する
			FrameLayout fl = new FrameLayout(this);
			fl.setLayoutParams(lParam);
			ValueView vv = new ValueView(this);
			mShowArea = new ShowArea(this, vv);
			mShowArea.setLayoutParams(lParam);
			// FrameLayoutにShowAreaとValueViewを追加
			fl.addView(mShowArea);
			fl.addView(vv);
			// LinearLayoutにFrameLayoutを追加
			rootLayout.addView(fl);

			// タップエリア作成、LinearLayoutへ登録
			mTapArea = new TapArea(this, mShowArea);
			mTapArea.setLayoutParams(lParam);
			rootLayout.addView(mTapArea);
		}

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
		Intent intent;
		switch (item.getItemId()) {
		case R.id.mReset:
			mShowArea.reset();
			return true;
		case R.id.mOption:
			// インテントの生成
			intent = new Intent(this, PrefActivity.class);

			// アクティビティの呼び出し
			startActivityForResult(intent, REQUEST_OPTION);

			return true;
		case R.id.mAbout:
			// インテントの生成
			intent = new Intent(this, AboutActivity.class);

			// アクティビティの呼び出し
			startActivity(intent);
			return true;
		}
		return false;
	}

	// アクティビティ呼び出し結果の取得
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == REQUEST_OPTION && resultCode == RESULT_OK) {
			// インテントからのパラメータ取得
			int bpm = Integer.parseInt(sharedPreferences.getString(getString(R.string.target_bpm_key), "0"));
			mShowArea.setTargetBpm(bpm);
		}
	}

	@Override
	protected void onPause() {
		if (mShowArea != null)
			mShowArea.stopScroll();
		super.onPause();
	}

}

package com.demo.activitymanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Context context;
	private TextView textResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		initialLayout();

		textResult.setText(getTopAppPackage(context));
	}

	private void initialLayout() {
		textResult = (TextView) findViewById(R.id.textResult);
	}

	/**
	 * 获取最前端应用的包名
	 * 
	 * PERMISSION_REQUIRED:NULL
	 * 
	 * @param context
	 * @return
	 */
	private String getTopAppPackage(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName componentName = activityManager.getRunningTasks(1)
					.get(0).topActivity;
			return Build.VERSION.SDK_INT + "\n getTopPackage:"
					+ componentName.getPackageName();
		} else {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName componentName = activityManager.getRunningTasks(1)
					.get(0).topActivity;
			return "\n getTopPackage:" + componentName.getPackageName();
		}
	}

}

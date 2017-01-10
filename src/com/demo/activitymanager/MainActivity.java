package com.demo.activitymanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.demo.activitymanager.service.TestService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
	}

	private void initialLayout() {
		textResult = (TextView) findViewById(R.id.textResult);
		Button btnGet = (Button) findViewById(R.id.btnGet);
		btnGet.setOnClickListener(myOnClickListener);

		Button btnStartService = (Button) findViewById(R.id.btnStartService);
		btnStartService.setOnClickListener(myOnClickListener);

		Button btnStopService = (Button) findViewById(R.id.btnStopService);
		btnStopService.setOnClickListener(myOnClickListener);
	}

	/**
	 * 获取最前端应用的包名
	 * 
	 * PERMISSION_REQUIRED:NULL
	 * 
	 * RESULT:23 com.demo.activitymanager
	 * 
	 * @param context
	 * @return
	 */
	private String getTopAppPackage(Context context) {
		// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
		return Build.VERSION.SDK_INT + "\n getTopPackage:"
				+ componentName.getPackageName();
	}

	/**
	 * 获取栈顶Activity及其所属进程
	 * 
	 * PERMISSION_REQUIRED:NULL
	 * 
	 * RESULT:MainActivity,com.demo.activitymanager
	 * 
	 * @param context
	 * @return
	 */
	public static String getTopActivityNameAndProcessName(Context context) {
		String processName = null;
		String topActivityName = null;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			String topActivityClassName = f.getClassName();
			String temp[] = topActivityClassName.split("\\.");
			// 栈顶Activity的名称
			topActivityName = temp[temp.length - 1];
			int index = topActivityClassName.lastIndexOf(".");
			// 栈顶Activity所属进程的名称
			processName = topActivityClassName.substring(0, index);

		}
		return "\n\n getTopActivityNameAndProcessName:" + topActivityName + ","
				+ processName;
	}

	/**
	 * 获取应用的进程信息
	 * 
	 * PERMISSION_REQUIRED:NULL
	 * 
	 * RESULT:processName=com.demo.activitymanager pid=4317 uid=10311
	 * memorySize=1696kb
	 * 
	 * @param context
	 * @return
	 */
	private String getRunningAppProcessInfo(Context context) {
		String result = "\n\n getRunningAppProcessInfo:";
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 获得系统里正在运行的所有进程
		List<RunningAppProcessInfo> runningAppProcessesList = activityManager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
			int pid = runningAppProcessInfo.pid; // 进程ID号
			int uid = runningAppProcessInfo.uid; // 用户ID
			String processName = runningAppProcessInfo.processName; // 进程名
			int[] pids = new int[] { pid };
			Debug.MemoryInfo[] memoryInfo = activityManager
					.getProcessMemoryInfo(pids);
			int memorySize = memoryInfo[0].dalvikPrivateDirty; // 占用的内存
			result += "\n\tprocessName=" + processName + "\tpid=" + pid
					+ "\tuid=" + uid + "\tmemorySize=" + memorySize + "kb";
		}
		return result;
	}

	/**
	 * 是否正在前台
	 * 
	 * @return
	 */
	public boolean isRunningForeground() {
		String packageName = getPackageName(this);
		String topActivityClassName = getTopActivityName(this);
		System.out.println("packageName=" + packageName
				+ ",topActivityClassName=" + topActivityClassName);
		if (packageName != null && topActivityClassName != null
				&& topActivityClassName.startsWith(packageName)) {
			return true;
		} else {
			return false;
		}
	}

	public String getTopActivityName(Context context) {
		String topActivityClassName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE));
		// android.app.ActivityManager.getRunningTasks(int maxNum)
		// int maxNum--->The maximum number of entries to return in the list
		// 即最多取得的运行中的任务信息(RunningTaskInfo)数量
		List<RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			topActivityClassName = f.getClassName();

		}
		return topActivityClassName;
	}

	public String getPackageName(Context context) {
		String packageName = context.getPackageName();
		return packageName;
	}

	/**
	 * 判断服务是否在运行中
	 * 
	 * @param context
	 * 
	 * @param serviceName
	 * 
	 * @return
	 */
	private boolean isServiceRunning(Context context, String serviceName) {
		if (!TextUtils.isEmpty(serviceName) && context != null) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ArrayList<RunningServiceInfo> runningServiceInfoList = (ArrayList<RunningServiceInfo>) activityManager
					.getRunningServices(1000);
			for (Iterator<RunningServiceInfo> iterator = runningServiceInfoList
					.iterator(); iterator.hasNext();) {
				RunningServiceInfo runningServiceInfo = (RunningServiceInfo) iterator
						.next();
				Log.v("AZ", "[SERVICE]"
						+ runningServiceInfo.service.getClassName().toString());
				if (serviceName.equals(runningServiceInfo.service
						.getClassName().toString())) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;
	}

	MyOnClickListener myOnClickListener = new MyOnClickListener();

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnGet:
				textResult
						.setText(getTopAppPackage(context)
								+ getTopActivityNameAndProcessName(context)
								+ getRunningAppProcessInfo(context)
								+ "\n\nisRunningForeground:"
								+ isRunningForeground()
								+ "\n\nisServiceRunning:"
								+ isServiceRunning(context,
										"com.demo.activitymanager.service.TestService"));
				break;

			case R.id.btnStartService:
				Intent intentStartService = new Intent(MainActivity.this,
						TestService.class);
				startService(intentStartService);
				break;

			case R.id.btnStopService:
				Intent intentStopService = new Intent(MainActivity.this,
						TestService.class);
				stopService(intentStopService);
				break;

			default:
				break;
			}

		}

	}

}

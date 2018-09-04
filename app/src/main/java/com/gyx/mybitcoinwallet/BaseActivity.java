package com.gyx.mybitcoinwallet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by gyx on 2018/8/30.
 */
public class BaseActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks, Serializable {
	/**
	 * 请求CAMERA权限码
	 */
	public static final int REQUEST_CAMERA_PERM = 101;

	//打开扫描界面请求码
	public static final int REQUEST_CODE = 0x01;
	public ProgressDialog progressDialog;
	public Animation enterAnim;

	public Animation exitAnim;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}





	@Override
	protected void onResume() {
		super.onResume();
//		initTitle();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}



	/**
	 * 获取状态栏的高度
	 *
	 * @return
	 */
	public int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void setStatusBar(View view) {
		int statusHeight = getStatusBarHeight();
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = statusHeight;
		view.setLayoutParams(layoutParams);
	}

	///////////////////////////////////////////////////////////////////////////
	//权限回调
	///////////////////////////////////////////////////////////////////////////
	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
	}

	/**
	 * 请求失败回调
	 *
	 * @param requestCode
	 * @param perms
	 */
	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//
			new AppSettingsDialog.Builder(this).build().show();
		}
	}
	///////////////////////////////////////////////////////////////////////////
	// 权限回调
	///////////////////////////////////////////////////////////////////////////

	/**
	 * EsayPermissions接管权限处理逻辑
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}







}


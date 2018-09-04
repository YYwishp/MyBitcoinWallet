package com.gyx.mybitcoinwallet.zxing.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.gyx.mybitcoinwallet.R;
import com.gyx.mybitcoinwallet.zxing.camera.CameraManager;
import com.gyx.mybitcoinwallet.zxing.decoding.CaptureActivityHandler;
import com.gyx.mybitcoinwallet.zxing.decoding.InactivityTimer;
import com.gyx.mybitcoinwallet.zxing.utils.CodeUtils;
import com.gyx.mybitcoinwallet.zxing.utils.ImageUtil;
import com.gyx.mybitcoinwallet.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends AppCompatActivity implements Callback,EasyPermissions.PermissionCallbacks{
	//	private Button cancelScanButton;
	public static final int RESULT_CODE_QR_SCAN = 0xA1;
	public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
	/**
	 * 选择系统图片Request Code
	 */
	public static final int REQUEST_IMAGE = 112;
	private static final int REQUEST_CODE_SCAN_GALLERY = 100;
	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;
	/**
	 * 请求READ_RESTORE权限码
	 */
	public static final int REQUEST_READ_RESTORE_PERM = 102;
	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private ImageView back;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private boolean vibrate;
	private ProgressDialog mProgress;
	private String photo_path;
	private Bitmap scanBitmap;
	private SurfaceHolder surfaceHolder;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication(),this);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_content);
		back = (ImageView) findViewById(R.id.scanner_toolbar_back);
		ImageView more = (ImageView) findViewById(R.id.scanner_toolbar_more);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				readExternalStorageTask();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
		surfaceHolder = surfaceView.getHolder();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	/**
	 *
	 * 请求权限
	 */
	@AfterPermissionGranted(REQUEST_READ_RESTORE_PERM)
	public void readExternalStorageTask() {
		if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
			// Have permission, do the thing!
			//打开手机中的相册
			/*Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
			innerIntent.setType("image*//*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
			startActivityForResult(wrapperIntent, REQUEST_CODE_SCAN_GALLERY);*/
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_IMAGE);
		} else {
			// Ask for one permission
			EasyPermissions.requestPermissions(this, "需要请求读取文件权限", REQUEST_READ_RESTORE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
		}
	}






	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			try {
				CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
					@Override
					public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
						//Toast.makeText(CaptureActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, result);
                        // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
                        //            bundle.putParcelable("bitmap", barcode);
                        //            Logger.d("saomiao",resultString);
                        resultIntent.putExtras(bundle);
                        setResult(RESULT_OK, resultIntent);
                        finish();

					}

					@Override
					public void onAnalyzeFailed() {
						Toast.makeText(CaptureActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 *
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		//FIXME
		if (TextUtils.isEmpty(resultString)) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		} else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, resultString);
			// 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
//            bundle.putParcelable("bitmap", barcode);
//            Logger.d("saomiao",resultString);
			resultIntent.putExtras(bundle);
			setResult(RESULT_OK, resultIntent);



		}
		finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {

		if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//
			new AppSettingsDialog.Builder(this).build().show();
		}
	}
}
package com.gyx.mybitcoinwallet.import_wallet;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gyx.mybitcoinwallet.BaseActivity;
import com.gyx.mybitcoinwallet.R;
import com.gyx.mybitcoinwallet.zxing.activity.CaptureActivity;
import com.gyx.mybitcoinwallet.zxing.utils.CommonUtil;

import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ImportWalletActivity extends BaseActivity implements View.OnClickListener {

	private EditText edMnemonic;
	private Button btScanMnemonic;
	private Button btImportMnemonic;
	private TextView tvImportState1;
	private EditText edPrivatekey;
	private Button btScanPrivatekey;
	private Button btImportPrivatekey;
	private TextView tvImportState2;
	private int resultType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_wallet);
		initView();
	}

	private void initView() {
		edMnemonic = (EditText) findViewById(R.id.ed_mnemonic);
		//扫描助记词
		btScanMnemonic = (Button) findViewById(R.id.bt_scan_mnemonic);
		btImportMnemonic = (Button) findViewById(R.id.bt_import_mnemonic);
		tvImportState1 = (TextView) findViewById(R.id.tv_import_state_1);
		//
		edPrivatekey = (EditText) findViewById(R.id.ed_privatekey);
		//扫描私钥
		btScanPrivatekey = (Button) findViewById(R.id.bt_scan_privatekey);
		btImportPrivatekey = (Button) findViewById(R.id.bt_import_privatekey);
		tvImportState2 = (TextView) findViewById(R.id.tv_import_state_2);
		//
		btScanMnemonic.setOnClickListener(this);
		btImportMnemonic.setOnClickListener(this);
		btScanPrivatekey.setOnClickListener(this);
		btImportPrivatekey.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//助记词扫描
			case R.id.bt_scan_mnemonic:
				resultType = 0;
				cameraTask();
				break;
			//助记词导入
			case R.id.bt_import_mnemonic:
				try {
					String trim = edMnemonic.getText().toString().trim();
					if (!TextUtils.isEmpty(trim)) {
						importFromMnemonic(trim);
					}
				} catch (UnreadableWalletException e) {
					e.printStackTrace();
				}
				break;
			//私钥扫描
			case R.id.bt_scan_privatekey:
				resultType = 1;
				cameraTask();
				break;
			//私钥导入
			case R.id.bt_import_privatekey:
				if (!TextUtils.isEmpty(edPrivatekey.getText().toString())) {
					importFromPrivateKey(edPrivatekey.getText().toString().trim());
				}
				break;
		}
	}

	public void importFromMnemonic(String seedCode) throws UnreadableWalletException {
//		NetworkParameters params = TestNet3Params.get();//测试网络
		MainNetParams params = MainNetParams.get();//正式网络
		String passphrase = "";
		Long creationtime = System.currentTimeMillis() / 1000L;
		DeterministicSeed seed = new DeterministicSeed(seedCode, null, passphrase, creationtime);
		DeterministicKey hd = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
		//分层
		hd = HDKeyDerivation.deriveChildKey(hd, new ChildNumber(44, true));
		hd = HDKeyDerivation.deriveChildKey(hd, new ChildNumber(0, true));
		hd = HDKeyDerivation.deriveChildKey(hd, new ChildNumber(0, true));
		hd = HDKeyDerivation.deriveChildKey(hd, new ChildNumber(0, false));
		hd = HDKeyDerivation.deriveChildKey(hd, new ChildNumber(0, false));
		System.out.println("私钥哈希：" + hd.getPrivateKeyAsHex());
		System.out.println("私钥：" + hd.getPrivateKeyAsWiF(params));
		ECKey ecKey = ECKey.fromPrivate(hd.getPrivKey());
		LegacyAddress address = LegacyAddress.fromKey(params, ecKey);
		System.out.println("普通地址：" + address.toString());
		SegwitAddress address2 = SegwitAddress.fromKey(params, ecKey);
		System.out.println("隔离地址：" + address2.toString());
		System.out.println("隔离地址类型：" + address2.getOutputScriptType());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("助记词：" + seedCode + "\n")
				.append("私钥：" + hd.getPrivateKeyAsWiF(params) + "\n")
				.append("普通地址：" + address.toString() + "\n")
				.append("隔离地址：" + address2.toString() + "\n")
				.append("隔离地址类型：" + address2.getOutputScriptType());
		tvImportState1.setText(stringBuilder.toString());
	}

	public void importFromPrivateKey(String privatekey) {
		MainNetParams mainNetParams = MainNetParams.get();
		ECKey key = DumpedPrivateKey.fromBase58(MainNetParams.get(), privatekey).getKey();
//		BigInteger privKey = Base58.decodeToBigInteger(privatekey);
//		ECKey key = ECKey.fromPrivate(privKey);
		SegwitAddress segwitAddress_1 = SegwitAddress.fromKey(mainNetParams, key);
		LegacyAddress address = LegacyAddress.fromKey(mainNetParams, key);
		System.out.println("----私钥: " + privatekey);
		System.out.println("----普通地址: " + address.toBase58());
		System.out.println("----隔离地址: " + segwitAddress_1.toString());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("私钥：" + privatekey + "\n")
				.append("普通地址：" + address.toBase58() + "\n")
				.append("隔离地址：" + segwitAddress_1.toString() + "\n")
				.append("隔离地址类型：" + segwitAddress_1.getOutputScriptType());
		tvImportState2.setText(stringBuilder.toString());
	}

	///////////////////////////////////////////////////////////////////////////
	//摄像头扫码部分
	///////////////////////////////////////////////////////////////////////////
	@AfterPermissionGranted(REQUEST_CAMERA_PERM)
	public void cameraTask() {
		if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
			// Have permission, do the thing!
			onViewClick();
		} else {
			// Ask for one permission
			EasyPermissions.requestPermissions(this, "需要请求camera权限", REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
		}
	}

	/**
	 * 打开摄像头
	 */
	private void onViewClick() {
		if (CommonUtil.isCameraCanUse()) {
			Intent intent = new Intent(this, CaptureActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		} else {
			Toast.makeText(this, "打开摄像头权限", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描完的结果，之后就是恢复钱包
		//扫描结果回调
		if (resultCode == RESULT_OK) { //RESULT_OK = -1
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("qr_scan_result");
			//将扫描出的信息显示出来
			//Toast.makeText(this, scanResult, Toast.LENGTH_SHORT).show();
			switch (resultType) {
				case 0:
					edMnemonic.setText(scanResult);
					break;
				case 1:
					edPrivatekey.setText(scanResult);
					break;
			}
		}
	}
}



































package com.gyx.mybitcoinwallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gyx.mybitcoinwallet.import_wallet.ImportWalletActivity;
import com.gyx.mybitcoinwallet.transaction.TransactionActivity;

public class MainActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);







	}

	public void importMnemonicsOrPrivateKey(View view) {


		startActivity(new Intent(this,ImportWalletActivity.class));





	}

	public void transaction(View view) {


		startActivity(new Intent(this,TransactionActivity.class));



	}

	public void createWallet(View view) {
	}
}

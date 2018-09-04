package com.gyx.mybitcoinwallet;


import com.gyx.mybitcoinwallet.bean.AddressBalanceBean;
import com.gyx.mybitcoinwallet.callback.DialogCallback;
import com.gyx.mybitcoinwallet.callback.OnDownLoadListener;
import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by gyx on 2017/10/11.
 */
public class RequestUtils {
	private static RequestUtils instance;


	private RequestUtils() {
	}

	/**
	 * 单一实例
	 */
	public static RequestUtils getInstance() {
		if (instance == null) {
			instance = new RequestUtils();
		}
		return instance;
	}

	private void setHost() {
//		String host = (String) SPUtils.get("host", "");
//		if (TextUtils.isEmpty(host)) {
//		} else {
//			HOST = host;
//		}
	}

	/**
	 * 历史记录
	 *
	 * @param active             就是地址
	 * @param onDownDataListener
	 */
	public void getAddressBalance(String active, final OnDownLoadListener<AddressBalanceBean> onDownDataListener) {
		setHost();
		//请求网络
		OkGo.get(Urls.BALANCE)//
				.tag(this)//
				.params("active", active)//
				.params("cors", true)//
				.execute(new DialogCallback<AddressBalanceBean>() {
					@Override
					public void onSuccess(AddressBalanceBean bean, Call call, Response response) {
						onDownDataListener.onSuccess(bean);
					}
//

					@Override
					public AddressBalanceBean convertSuccess(Response response) throws Exception {
						String json = response.body().string();
						if (json == null) {
							return null;
						}
						JSONObject jsonObject = new JSONObject(json);
						Iterator<String> iterator = jsonObject.keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							if (key != null) {
								JSONObject jsb = jsonObject.getJSONObject(key);
								AddressBalanceBean bean = new AddressBalanceBean();
								bean.setFinal_balance(jsb.getString("final_balance"));
								bean.setN_tx(jsb.getString("n_tx"));
								bean.setTotal_received(jsb.getString("total_received"));
								return bean;
							}
						}
						return null;


					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						onDownDataListener.onFailed(e);
						super.onError(call, response, e);
					}
				});
	}

	/**
	 * 创建钱包
	 *
	 * @param onDownDataListener
	 */
	public void getUnspent( String active, final OnDownLoadListener<String> onDownDataListener) {
		setHost();
		//请求网络
		OkGo.get(Urls.UNSPENT)//
				.tag(this)//
				.params("active", active)//
				.params("cors", true)//
				.execute(new DialogCallback<String>() {
					@Override
					public void onSuccess(String bean, Call call, Response response) {
						onDownDataListener.onSuccess(bean);

					}

					@Override
					public String convertSuccess(Response response) throws Exception {
						String string = response.body().string();
						return string;
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						onDownDataListener.onFailed(e);
						super.onError(call, response, e);
					}
				});
	}
}




























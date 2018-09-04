package com.gyx.mybitcoinwallet.callback;

/**
 * Created by gyx on 2017/10/11.
 */
public interface OnDownLoadListener<T>  {
	void onSuccess(T t);

	void onFailed(Exception e);



}
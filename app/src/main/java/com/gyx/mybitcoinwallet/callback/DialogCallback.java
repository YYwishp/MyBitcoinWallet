package com.gyx.mybitcoinwallet.callback;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by GYX on 2016/10/27.
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {
	public DialogCallback(Activity activity) {
		super();
		// initDialog(activity);
	}

	public DialogCallback() {
	}

    /*private void initDialog(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
    }
*/

	@Override
	public void onBefore(BaseRequest request) {
		super.onBefore(request);







       /* //网络请求前显示对话框
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }*/
	}

  /*  @Override
    public void parseError(Call call, Exception e) {
        super.parseError(call, e);

    }*/

	@Override
	public void onError(Call call, Response response, Exception e) {
		super.onError(call, response, e);
		//ToastUtil.toast( "网络不给力啊╮(╯﹏╰）╭");
	}

	@Override
	public void onAfter(@Nullable T t, @Nullable Exception e) {
		super.onAfter(t, e);
	    /*//网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }*/
	}
}
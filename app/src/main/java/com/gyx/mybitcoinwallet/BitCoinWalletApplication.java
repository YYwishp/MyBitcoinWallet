package com.gyx.mybitcoinwallet;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;

/**
 * Created by gyx on 2018/4/3.
 */
public class BitCoinWalletApplication extends Application {
	public static final String ACTION_WALLET_REFERENCE_CHANGED = BitCoinWalletApplication.class.getPackage().getName() + ".wallet_reference_changed";

	public static String COINWALL_ENV = "stagging";

	private static Context instance;
	public static Context getInstance() {
		return instance;
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);

	}



	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		//====初始化网络
		initNetModular();
	}

	private void initNetModular() {
		//---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
		HttpHeaders headers = new HttpHeaders();
//		headers.put("api-version", Urls.REQUEST_API);    //header不支持中文
//		headers.put("Content-Type", "application/json");
//		headers.put("CLIENT-V", "5");//android手机
//        HttpParams params = new HttpParams();
		//params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
		//params.put("commonParamsKey2", "这里支持中文参数");
		//-----------------------------------------------------------------------------------//
		//必须调用初始化
		OkGo.init(this);
		//以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
		//好处是全局参数统一,特定请求可以特别定制参数
		try {
			//修改最大并发数
			OkGo.getInstance().getOkHttpClient().dispatcher().setMaxRequestsPerHost(100);
			//以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
			OkGo.getInstance()
					//打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
					.debug("OkGo")
					//如果使用默认的 60秒,以下三行也不需要传
					.setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
					.setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
					.setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间
					//可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy0216/
					.setCacheMode(CacheMode.DEFAULT)
					//可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
					.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
					//可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
					.setRetryCount(0)
					//如果不想让框架管理cookie,以下不需要
//                  .setCookieStore(new MemoryCookieStore())                //cookie使用内存缓存（app退出后，cookie消失）
					.setCookieStore(new PersistentCookieStore())       //cookie持久化存储，如果cookie不过期，则一直有效
					//可以设置https的证书,以下几种方案根据需要自己设置
//                    .setCertificates()                                  //方法一：信任所有证书（选一种即可）
//                    .setCertificates(getAssets().open("mykey.cer"))      //方法二：也可以自己设置https证书（选一种即可）
					//.setCertificates(getAssets().open("gyx_prod_bks.bks"), "m4HzRgMRQWxg9xZutugHgFu4GhsZanSwOpWZIeBnnPs=", getAssets().open("gyx_pro_cer.cer"))//方法三：传入bks证书,密码,和cer证书,支持双向加密
//                    .setCertificates(getAssets().open("gyx.bks"), "123456", getAssets().open("333.cer"))//方法三：传入bks证书,密码,和cer证书,支持双向加密
					//可以添加全局拦截器,不会用的千万不要传,错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })
					//这两行同上,不需要就不要传
					.addCommonHeaders(headers);                                  //设置全局公共头
			//.addCommonParams(params);                                          //设置全局公共参数
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

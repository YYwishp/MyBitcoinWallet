package com.gyx.mybitcoinwallet;

/**
 * Created by GYX on 2017/9/22.
 */
public class Urls {
	private static String HOST;

	private static final String STAGGING_HOST = "testnet.blockchain.info";

	private static final String PRODUCT_HOST = "blockchain.info";
	public static final String REQUEST_API = "1.7.0";

	static String WEB_HOST;

	/**
	 * 更换服务器环境
	 */
	static {

		switch (BitCoinWalletApplication.COINWALL_ENV) {
			case "product":
				HOST = PRODUCT_HOST;
				//WEB_HOST = "www.coinwall.io:3101";
				break;
			case "stagging":
				HOST = STAGGING_HOST;
				//WEB_HOST = "www.coinwall.io:3101";
				break;
		}
		String SCHEMA =  "https://" ;
		HOST = SCHEMA + HOST;
		WEB_HOST = SCHEMA + WEB_HOST;
	}
	/**
	 * 比特币unspend
	 */
	public static final String UNSPENT = HOST + "/zh-cn/unspent";




	/**
	 * 获取balance
	 */
	public static final String BALANCE = HOST + "/balance";







}





















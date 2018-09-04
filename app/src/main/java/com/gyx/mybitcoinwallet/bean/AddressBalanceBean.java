package com.gyx.mybitcoinwallet.bean;

/**
 * Created by gyx on 2018/4/3.
 */
public class AddressBalanceBean {
	private String final_balance;
	private String n_tx;
	private String total_received;

	public String getFinal_balance() {
		return final_balance;
	}

	public void setFinal_balance(String final_balance) {
		this.final_balance = final_balance;
	}

	public String getN_tx() {
		return n_tx;
	}

	public void setN_tx(String n_tx) {
		this.n_tx = n_tx;
	}

	public String getTotal_received() {
		return total_received;
	}

	public void setTotal_received(String total_received) {
		this.total_received = total_received;
	}
}


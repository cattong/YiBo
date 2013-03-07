package com.shejiaomao.weibo.widget;

import java.beans.PropertyChangeEvent;

import com.shejiaomao.weibo.db.LocalAccount;


public class ValueSetEvent extends PropertyChangeEvent {

    private static final long serialVersionUID = 2355912775818972678L;

	private LocalAccount account;
	private Action action;

	private Object transferValue = null;
    public ValueSetEvent(Object source, String propertyName, Object oldValue,
			Object newValue, LocalAccount account) {
		super(source, propertyName, oldValue, newValue);

		this.account = account;
	}

    public enum Action {
		ACTION_INIT_ADAPTER,
		ACTION_RECLAIM_MEMORY,
		ACTION_MY_HOME_DELETE
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Object getTransferValue() {
		return transferValue;
	}

	public void setTransferValue(Object transferValue) {
		this.transferValue = transferValue;
	}
}

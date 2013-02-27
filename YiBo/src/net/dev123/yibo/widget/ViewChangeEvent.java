package net.dev123.yibo.widget;

import java.beans.PropertyChangeEvent;

import net.dev123.yibo.db.LocalAccount;
import android.view.View;

public class ViewChangeEvent extends PropertyChangeEvent {

    private static final long serialVersionUID = -4872450125770935995L;

	View view;
	LocalAccount account;

    public ViewChangeEvent(Object source, String propertyName, Object oldValue,
			Object newValue, View view, LocalAccount account) {
		super(source, propertyName, oldValue, newValue);

		this.view = view;
		this.account = account;
	}

	public View getView() {
		return view;
	}

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

}

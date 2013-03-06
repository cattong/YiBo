package com.cattong.sns;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;


public class TokenConfig {
    public static final byte[] KEY_BYTES  = { 0x6f, 0x68, 0x6d, 0x79, 0x67, 0x6f, 0x64, 0x21 };

    public static Map<ServiceProvider, Authorization> oauthMap;
    public static final ServiceProvider currentProvider = ServiceProvider.Facebook;

    public static final String[][] oauths = {
    	{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},
    	{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},{"", ""},
    	{"", ""}, //占位
    	//人人;
    	{"135210|6.a13dc88bdec7e56194e4ce253ab7e4c1.2592000.1321711200-423619944", "2594154"},
    	//开心
    	{"6215376_fea14b2a44cf385002b4e2b07dc16ecd", "259200"},
    	//QQ空间
    	{"A9C64F79CF62B303B5B2A68240CC503A", "100000"},
    	//Facebook
    	{"AAAESQdN0qU4BAPOvby3eBOH56OZCVQ8AWkFtAHzym0ZAcNI8wYSAGFc4B4adeExuLkrPrut0rzI8oGXjkfexEgpYms0X0M7JMcD0AArwZDZD", "0"}
    };

	static {
		oauthMap = new HashMap<ServiceProvider, Authorization>();
		for (int i = ServiceProvider.RenRen.getSpNo(); i < oauths.length; i++) {
	    	ServiceProvider sp = getServiceProvider(i);
			Authorization oauth = new Authorization(sp);
			oauth.setAccessToken(oauths[i][0]);
			oauth.setExpiredAt(new Date(Long.valueOf(oauths[i][1])));

			oauthMap.put(sp, oauth);
	    }
	}

	public static ServiceProvider getServiceProvider(int providerNo) {
		ServiceProvider[] providers = ServiceProvider.values();
		ServiceProvider sp = null;
		for (ServiceProvider temp : providers) {
			if (temp.getSpNo() == providerNo) {
				sp = temp;
				break;
			}
		}

		return sp;
	}

	public static Sns getSns(ServiceProvider sp) {
		Sns sns = null;
		if (sp == null) {
			return sns;
		}
		Authorization oauth = oauthMap.get(sp);
		sns = SnsFactory.getInstance(oauth);
		return sns;
	}

}

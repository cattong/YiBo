package net.dev123.yibome.api;

import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.ConfigApp;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-31 下午2:09:32
 **/
public interface ConfigAppService {
	List<ConfigApp> getMyConfigApps() throws LibException;
	List<ConfigApp> getMyConfigApps(ServiceProvider sp) throws LibException;
}

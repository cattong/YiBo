package net.dev123.yibome;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.util.DateTimeUtil;
import net.dev123.commons.util.EncryptUtil;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;
import net.dev123.yibome.converter.AccountJSONConverter;
import net.dev123.yibome.converter.ConfigAppConverter;
import net.dev123.yibome.converter.GroupJSONConverter;
import net.dev123.yibome.converter.PointLevelJSONConverter;
import net.dev123.yibome.converter.PointOrderInfoJSONConverter;
import net.dev123.yibome.converter.StatusJSONConverter;
import net.dev123.yibome.converter.StatusSyncJSONConverter;
import net.dev123.yibome.converter.StringListJSONConverter;
import net.dev123.yibome.converter.UserJSONConverter;
import net.dev123.yibome.entity.Account;
import net.dev123.yibome.entity.AccountSyncResult;
import net.dev123.yibome.entity.ConfigApp;
import net.dev123.yibome.entity.GroupSyncResult;
import net.dev123.yibome.entity.LocalGroup;
import net.dev123.yibome.entity.PointLevel;
import net.dev123.yibome.entity.PointOrderInfo;
import net.dev123.yibome.entity.StatusSyncResult;
import net.dev123.yibome.entity.SubscribeCatalog;
import net.dev123.yibome.entity.UserExtInfo;
import net.dev123.yibome.entity.UserGroup;
import net.dev123.yibome.entity.UserGroupSyncResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YiBoMeImpl extends YiBoMe {
	public YiBoMeImpl(Authorization auth) {
		super(auth);
	}

	@Override
	public AccountSyncResult syncAccounts(List<? extends Account> accounts) throws LibException{
		try {
			JSONArray jsonArray = AccountJSONConverter.toJSONArray(accounts);
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.POST, conf.getAccountSyncURL(), auth);
			httpRequestMessage.addParameter("accounts", jsonArray.toString());
			httpRequestMessage.addParameter("flag", 1); // 增加此标志，表明加密

			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			AccountSyncResult syncResult = new AccountSyncResult();
			if (!json.isNull("to_be_added")) {
				JSONArray toBeAddedArray = json.getJSONArray("to_be_added");
				List<Account> toBeAdded = AccountJSONConverter.toAccountList(toBeAddedArray);
				syncResult.setToBeAdded(toBeAdded);
			}
			if (!json.isNull("to_be_deleted")) {
				JSONArray toBeDeletedArray = json.getJSONArray("to_be_deleted");
				List<Account> toBeDeleted = AccountJSONConverter.toAccountList(toBeDeletedArray);
				syncResult.setToBeDeleted(toBeDeleted);
			}
			if (!json.isNull("to_be_updated")) {
				JSONArray toBeUpdatedArray = json.getJSONArray("to_be_updated");
				List<Account> toBeUpdated = AccountJSONConverter.toAccountList(toBeUpdatedArray);
				syncResult.setToBeUpdated(toBeUpdated);
			}
			return syncResult;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}

	@Override
	public GroupSyncResult syncGroups(List<LocalGroup> groups) throws LibException {
		try {
			JSONArray jsonArray = GroupJSONConverter.toJSONArray(groups);
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.POST, conf.getAccountSyncURL(), auth);
			httpRequestMessage.addParameter("groups", jsonArray.toString());
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			GroupSyncResult groupSyncResult = new GroupSyncResult();
			if (!json.isNull("to_be_added")) {
				JSONArray toBeAddedArray = json.getJSONArray("to_be_added");
				List<LocalGroup> toBeAdded = GroupJSONConverter.toGroupList(toBeAddedArray);
				groupSyncResult.setToBeAdded(toBeAdded);
			}
			if (!json.isNull("to_be_deleted")) {
				JSONArray toBeDeletedArray = json.getJSONArray("to_be_deleted");
				List<LocalGroup> toBeDeleted = GroupJSONConverter.toGroupList(toBeDeletedArray);
				groupSyncResult.setToBeDeleted(toBeDeleted);
			}
			if (!json.isNull("to_update_name")) {
				JSONArray toUpdateNameArray = json.getJSONArray("to_update_name");
				List<LocalGroup> toUpdateName = GroupJSONConverter.toGroupList(toUpdateNameArray);
				groupSyncResult.setToUpdateName(toUpdateName);
			}
			if (!json.isNull("to_update_id")) {
				JSONArray toUpdateIdArray = json.getJSONArray("to_update_id");
				List<LocalGroup> toUpdateId = GroupJSONConverter.toGroupList(toUpdateIdArray);
				groupSyncResult.setToUpdateId(toUpdateId);
			}
			return groupSyncResult;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public UserGroupSyncResult syncGroupUsers(Long groupId, List<UserGroup> userGroups, ServiceProvider sp) throws LibException{
		try {
			JSONArray jsonArray = new JSONArray();
			for (UserGroup userGroup : userGroups) {
				JSONObject json = new JSONObject();
				json.put("user_id", userGroup.getUserId());
				json.put("state", userGroup.getState());
				jsonArray.put(json);
			}
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.POST, conf.getAccountSyncURL(), auth);
			httpRequestMessage.addParameter("service_provider", sp.getServiceProviderNo());
			httpRequestMessage.addParameter("group_id", groupId);
			httpRequestMessage.addParameter("users", jsonArray.toString());
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			UserGroupSyncResult userGroupSyncResult = new UserGroupSyncResult();
			JSONObject json = new JSONObject(response);
			userGroupSyncResult.setGroupId(json.getLong("group_id"));
			userGroupSyncResult.setServiceProviderNo(json.getInt("service_provider"));
			if (!json.isNull("to_be_added")) {
				JSONArray toBeAddedArray = json.getJSONArray("to_be_added");
				List<String> toBeAdded = StringListJSONConverter.toStringList(toBeAddedArray);
				userGroupSyncResult.setToBeAdded(toBeAdded);
			}
			if (!json.isNull("to_be_deleted")) {
				JSONArray toBeDeletedArray = json.getJSONArray("to_be_deleted");
				List<String> toBeDeleted = StringListJSONConverter.toStringList(toBeDeletedArray);
				userGroupSyncResult.setToBeDeleted(toBeDeleted);
			}
			return userGroupSyncResult;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
	
	@Override
	public List<Status> getStatusSubscribe(SubscribeCatalog catalog, ServiceProvider sp, 
		Paging<Status> paging) throws LibException {
		if (catalog == null 
			|| catalog == SubscribeCatalog.NONE
			|| sp == null 
			|| paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getStatusSubscribeURL(), auth);
		httpRequestMessage.addParameter("serviceProvider", sp.getServiceProviderNo());
		httpRequestMessage.addParameter("catalog", catalog.getSubscribeCatalogNo());
		httpRequestMessage.addParameter("pageIndex", paging.getPageIndex());
		httpRequestMessage.addParameter("pageSize", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		List<Status> statusList = StatusJSONConverter.createStatusList(response);
		if (ListUtil.isEmpty(statusList) || statusList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}
		
		return statusList;
	}

	@Override
	public User getUserBaseInfo(String userId, ServiceProvider sp)
			throws LibException {
		if (StringUtil.isEmpty(userId) || sp == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getUserBaseInfoURL(), auth);
		httpRequestMessage.addParameter("userId", userId);
		httpRequestMessage.addParameter("serviceProvider", sp.getServiceProviderNo());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		User user = UserJSONConverter.createUser(response);
		return user;
	}

	@Override
	public UserExtInfo getUserExtInfo(String userId, ServiceProvider sp)
			throws LibException {
		if (StringUtil.isEmpty(userId) || sp == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getUserExtInfoURL(), auth);
		httpRequestMessage.addParameter("userId", userId);
		httpRequestMessage.addParameter("serviceProvider", sp.getServiceProviderNo());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		UserExtInfo userExtInfo = UserJSONConverter.createUserExtInfo(response);
		return userExtInfo;
	}

	@Override
	public List<StatusSyncResult> syncStatus(StatusUpdate updateStatus,
			String accountInfos) throws LibException {
		if (updateStatus == null 
			|| StringUtil.isEmpty(updateStatus.getStatus())
			|| StringUtil.isEmpty(accountInfos)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getStatusSyncURL(), auth);
			
		httpRequestMessage.addParameter("text", updateStatus.getStatus());
		httpRequestMessage.addParameter("accounts_info", accountInfos);
		if (updateStatus.getImage() != null) {
			httpRequestMessage.addParameter("image", updateStatus.getImage());
		}
		if (updateStatus.getLocation() != null) {
			httpRequestMessage.addParameter(
				"latitude", updateStatus.getLocation().getLatitude());
			httpRequestMessage.addParameter(
				"longitude", updateStatus.getLocation().getLongitude());
		}
			
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		return StatusSyncJSONConverter.createStatusSyncResultList(response);
	}

	@Override
	public List<StatusSyncResult> syncStatus(StatusUpdate updateStatus,
			List<? extends Account> accountList) throws LibException {
		if (updateStatus == null 
			|| StringUtil.isEmpty(updateStatus.getStatus())
			|| accountList == null
			|| accountList.size() < 1) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		JSONArray accountsInfoJsonArray = StatusSyncJSONConverter.createAccountInfos(accountList);
		return syncStatus(updateStatus, accountsInfoJsonArray.toString());
	}

	@Override
	public PointLevel getPoints() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getPointsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);		
		return PointLevelJSONConverter.createPointLevel(response);
	}
	
	@Override
	public PointOrderInfo addLoginPoints() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getLoginPointsAddURL(), auth);
		
		String sig = sign();
		httpRequestMessage.addParameter("sig", sig);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		return PointOrderInfoJSONConverter.createPointOrder(response);
	}
	
	private String sign(Object... params) {
		StringBuffer sb = new StringBuffer();
		if (params == null || params.length == 0) {
			return sb.toString();
		}
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			sb.append(param.toString());
			//if (i != params.length -1) {
			//	sb.append("|");
			//}
		}
		sb.append(DateTimeUtil.getShortFormat(new Date()));
		return EncryptUtil.getMD5(sb.toString());
	}

	public List<ConfigApp> getMyConfigApps() throws LibException {
		return getMyConfigApps(null);
	}
	
	public List<ConfigApp> getMyConfigApps(ServiceProvider sp) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, conf.getMyConfigAppsURL(), auth);
		if (sp != null) {
			httpRequestMessage.addParameter("service_provider_no", sp.getServiceProviderNo());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<ConfigApp> appList = ConfigAppConverter.createConfigAppList(response);
		return appList;
	}
}

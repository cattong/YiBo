package com.cattong.socialcat.impl.socialcat;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;

import com.cattong.commons.Constants;
import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.DateTimeUtil;
import com.cattong.commons.util.GMTDateGsonAdapter;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.SignatureUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TaskTypeGsonAdapter;
import com.cattong.entity.Account;
import com.cattong.entity.AppInfo;
import com.cattong.entity.AppTaskInfo;
import com.cattong.entity.DeviceInfo;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsOrderInfo;
import com.cattong.entity.SimCardInfo;
import com.cattong.entity.Status;
import com.cattong.entity.StatusCatalog;
import com.cattong.entity.StatusExtInfo;
import com.cattong.entity.TaskOrderInfo;
import com.cattong.entity.TaskType;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;
import com.cattong.entity.WithdrawOrderInfo;
import com.cattong.entity.WitkeyTaskInfo;
import com.cattong.socialcat.api.AccountService;
import com.cattong.socialcat.api.DeviceAppService;
import com.cattong.socialcat.api.OrderService;
import com.cattong.socialcat.api.PassportService;
import com.cattong.socialcat.api.StatusService;
import com.cattong.socialcat.api.TaskService;
import com.cattong.socialcat.api.UserService;
import com.cattong.socialcat.conf.SocialCatApiConfig;
import com.cattong.socialcat.conf.SocialCatApiConfigImpl;
import com.cattong.socialcat.converter.StatusJSONConverter;
import com.cattong.socialcat.converter.UserJSONConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SocialCat implements PassportService, AccountService, StatusService,
	UserService, OrderService, DeviceAppService, TaskService {
	
	protected final SocialCatApiConfig apiConf;
	protected Authorization auth;
	protected String clientVersion;
	
	protected ResponseHandler<String> responseHandler;
	private Gson gson;
	public SocialCat(Authorization auth) {
		this(auth, null);
	}
	
	public SocialCat(Authorization auth, String clientVersion) {
		this.auth = auth;
		this.clientVersion = clientVersion;
		this.apiConf = new SocialCatApiConfigImpl();
		this.responseHandler = new SocialCatResponseHandler();
		this.gson = getGson();
	}

	private Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.serializeNulls();
		
		//GSON对date的序列化和反序列化处理
		GMTDateGsonAdapter dateGsonAdapter = new GMTDateGsonAdapter();
		gsonBuilder.registerTypeAdapter(java.util.Date.class, dateGsonAdapter);
		gsonBuilder.registerTypeAdapter(java.sql.Timestamp.class, dateGsonAdapter);
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, dateGsonAdapter);
		//Gson对taskType的序列化和反序列化处理
		TaskTypeGsonAdapter taskTypeGsonAdapter = new TaskTypeGsonAdapter();
		gsonBuilder.registerTypeAdapter(TaskType.class, taskTypeGsonAdapter);
		
		return gsonBuilder.create();
	}
	
	@Override
	public Passport register(String email, String username, String password, int passportType) throws LibException {
		if (StringUtil.isEmpty(email) 
			|| StringUtil.isEmpty(username) 
			|| StringUtil.isEmpty(password)
			|| passportType < 0) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getRegisterURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("email", email);
		httpRequestWrapper.addParameter("username", username);
		httpRequestWrapper.addParameter("password", password);
		httpRequestWrapper.addParameter("passportType", passportType);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper,	responseHandler);
		return SocialCatJsonAdapter.createPassport(response, gson);	
	}

	@Override
	public Passport login(String username, String password) throws LibException {
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getLoginURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("username", username);
		httpRequestWrapper.addParameter("password", password);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper,	responseHandler);
		return SocialCatJsonAdapter.createPassport(response, gson);
	}
	
	@Override
	public Passport getPoints() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getPointsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);		
		
		return SocialCatJsonAdapter.createPassport(response, gson);
	}
	
	@Override
	public Passport updateEmail(String email, String password, String promoterId) throws LibException {
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getUpdateEmailURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("email", email);
		httpRequestWrapper.addParameter("password", password);
		if (StringUtil.isNotEmpty(promoterId)) {
		    httpRequestWrapper.addParameter("promoterId", promoterId);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);		
			
		return SocialCatJsonAdapter.createPassport(response, gson);
	}

	@Override
	public Passport checkIn() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getCheckInURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);		
				
		return SocialCatJsonAdapter.createPassport(response, gson);
	}

	@Override
	public List<Passport> findRankings() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getFindRankingsURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		return SocialCatJsonAdapter.createPassportList(response, gson);
	}
	
	@Override
	public Account syncAccount(Account account) throws LibException {
		if (account == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getSyncAccountURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		String accountJson = gson.toJson(account);
		httpRequestWrapper.addParameter("account", accountJson);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		return SocialCatJsonAdapter.createAccount(response, gson);
	}
	
	@Override
	public List<Account> syncAccountList() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getSyncAccountListURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		return SocialCatJsonAdapter.createAccountList(response, gson);
	}
	
	@Override
	public StatusExtInfo getStatusExtInfo(ServiceProvider sp, String statusId)
			throws LibException {
		if (sp == null || StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getStatusExtInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("sp", sp.getSpNo());
		httpRequestWrapper.addParameter("statusId", statusId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		StatusExtInfo statusExtInfo = StatusJSONConverter.createStatusExtInfo(response);
		return statusExtInfo;
	}
	
	@Override
	public List<Status> getStatusCatalog(StatusCatalog catalog, ServiceProvider sp, 
		Paging<Status> paging) throws LibException {
		if (catalog == null 
			|| catalog == StatusCatalog.Unknow
			|| sp == null 
			|| paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getStatusCatalogURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("sp", sp.getSpNo());
		httpRequestWrapper.addParameter("statusCatalog", catalog.getCatalogNo());
		httpRequestWrapper.addParameter("pageIndex", paging.getPageIndex());
		httpRequestWrapper.addParameter("pageSize", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		List<Status> statusList = StatusJSONConverter.createStatusList(response);
		if (ListUtil.isEmpty(statusList) || statusList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}
		
		return statusList;
	}

	@Override
	public List<Status> getMobilePhoto(ServiceProvider sp, Paging<Status> paging)
			throws LibException {
		if (sp == null || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getMobilePhotoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("sp", sp.getSpNo());
		httpRequestWrapper.addParameter("pageIndex", paging.getPageIndex());
		httpRequestWrapper.addParameter("pageSize", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		List<Status> statusList = StatusJSONConverter.createStatusList(response);
		if (ListUtil.isEmpty(statusList) || statusList.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);
		}
		
		return statusList;
	}
	
	@Override
	public User getUserBaseInfo(ServiceProvider sp, String userId)
			throws LibException {
		if (StringUtil.isEmpty(userId) || sp == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getUserBaseInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("userId", userId);
		httpRequestWrapper.addParameter("serviceProvider", sp.getSpNo());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		User user = UserJSONConverter.createUser(response);
		return user;
	}

	@Override
	public UserExtInfo getUserExtInfo(ServiceProvider sp, String userId)
			throws LibException {
		if (StringUtil.isEmpty(userId) || sp == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getUserExtInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("userId", userId);
		httpRequestWrapper.addParameter("serviceProvider", sp.getSpNo());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		UserExtInfo userExtInfo = UserJSONConverter.createUserExtInfo(response);
		return userExtInfo;
	}

	@Override
	public Passport addPointsOrderInfo(List<PointsOrderInfo> orderInfoList, PublicKey publicKey) throws LibException {
		if (ListUtil.isEmpty(orderInfoList) || publicKey == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getAddPointsOrderInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		String pointsOrderInfoListJson = gson.toJson(orderInfoList);
		httpRequestWrapper.addParameter("pointsOrderInfoList", pointsOrderInfoListJson);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientVersion", clientVersion);
		paramMap.put("access_token", auth.getAccessToken());		
		paramMap.put("pointsOrderInfoList", pointsOrderInfoListJson);
		
		String sign = SignatureUtil.sign(paramMap, publicKey);
		httpRequestWrapper.addParameter("sign", sign);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		Passport passport = SocialCatJsonAdapter.createPassport(response, gson);
		return passport;
	}
	
	@Override
	public WithdrawOrderInfo withdraw(String withdrawTo, int orderType, 
			int money, PublicKey publicKey) throws LibException {
		if (StringUtil.isEmpty(withdrawTo) 
			|| orderType < 0 
			|| money <= 0
			|| publicKey == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getWithdrawURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("withdrawTo", withdrawTo);
		httpRequestWrapper.addParameter("orderType", orderType);
		httpRequestWrapper.addParameter("money", money);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientVersion", clientVersion);
		paramMap.put("access_token", auth.getAccessToken());
		paramMap.put("withdrawTo", withdrawTo);
		paramMap.put("orderType", orderType);
		paramMap.put("money", money);
		String sign = SignatureUtil.sign(paramMap, publicKey);
		httpRequestWrapper.addParameter("sign", sign);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		WithdrawOrderInfo orderInfo = SocialCatJsonAdapter.createWithdrawOrderInfo(response, gson);
		return orderInfo;
	}
	
	@Override
	public List<WithdrawOrderInfo> findWithdrawOrderInfo(Date startedAt, Date endedAt) throws LibException {
		if (startedAt == null
			|| endedAt == null
			|| startedAt.after(endedAt)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getFindWithdrawOrderInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("startedAt", DateTimeUtil.getLongFormat(startedAt));
		httpRequestWrapper.addParameter("endedAt", DateTimeUtil.getLongFormat(endedAt));
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		List<WithdrawOrderInfo> withdrawOrderInfoList = null;
		withdrawOrderInfoList = SocialCatJsonAdapter.createWithdrawOrderInfoList(response, gson);
		
		return withdrawOrderInfoList;
	}

	@Override
	public Passport addPreloadTaskOrderInfo(List<TaskOrderInfo> taskOrderInfoList,
			PublicKey publicKey) throws LibException {
        if (ListUtil.isEmpty(taskOrderInfoList)
        	|| publicKey == null) {
        	throw new LibException(LibResultCode.E_PARAM_ERROR);
        }
        
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getAddPreloadTaskOrderInfoURL(), auth);
        httpRequestWrapper.addParameter("clientVersion", clientVersion);
        String taskOrderInfoListJson = gson.toJson(taskOrderInfoList);
        httpRequestWrapper.addParameter("source", Constants.SOURCE_PRELOADER);
		httpRequestWrapper.addParameter("taskOrderInfoList", taskOrderInfoListJson);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientVersion", clientVersion);
		paramMap.put("access_token", auth.getAccessToken());
		paramMap.put("source", Constants.SOURCE_PRELOADER);
		paramMap.put("taskOrderInfoList", taskOrderInfoListJson);
		String sign = SignatureUtil.sign(paramMap, publicKey);
		httpRequestWrapper.addParameter("sign", sign);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		Passport passport = SocialCatJsonAdapter.createPassport(response, gson);
		return passport;
	}
	
	@Override
	public TaskOrderInfo activePreloadTaskOrderInfo(long taskId, String deviceId,
			PublicKey publicKey) throws LibException {
		if (taskId < 0
			|| StringUtil.isEmpty(deviceId)
		    || publicKey == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getActivePreloadTaskOrderInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("source", Constants.SOURCE_PRELOADER);
		httpRequestWrapper.addParameter("taskId", taskId);
		httpRequestWrapper.addParameter("deviceId", deviceId);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientVersion", clientVersion);
		paramMap.put("access_token", auth.getAccessToken());
		paramMap.put("source", Constants.SOURCE_PRELOADER);
		paramMap.put("taskId", taskId);
		paramMap.put("deviceId", deviceId);
		String sign = SignatureUtil.sign(paramMap, publicKey);
		httpRequestWrapper.addParameter("sign", sign);
		
        String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		TaskOrderInfo taskOrderInfo = SocialCatJsonAdapter.createTaskOrderInfo(response, gson);
		return taskOrderInfo;
	}
	
	@Override
	public DeviceInfo addDeviceInfo(DeviceInfo deviceInfo) throws LibException {
		if (deviceInfo == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getAddDeviceInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("deviceInfo", gson.toJson(deviceInfo));
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		DeviceInfo resultDeviceInfo = SocialCatJsonAdapter.createDeviceInfo(response, gson);
		
		return resultDeviceInfo;
	}
	
	@Override
	public DeviceInfo addAppInfo(String deviceId, List<AppInfo> appInfoList) throws LibException {
		if (StringUtil.isEmpty(deviceId) || ListUtil.isEmpty(appInfoList)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getAddAppInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("deviceId", deviceId);
		httpRequestWrapper.addParameter("appInfoList", gson.toJson(appInfoList));
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		DeviceInfo deviceInfo = SocialCatJsonAdapter.createDeviceInfo(response, gson);
		
		return deviceInfo;
	}

	@Override
	public List<AppTaskInfo> findPreloadAppTaskInfo() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, apiConf.getFindPreloadAppTaskInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<AppTaskInfo> appTaskInfoList = SocialCatJsonAdapter.createAppTaskInfoList(
			response, gson);
		
		return appTaskInfoList;
	}

	@Override
	public List<WitkeyTaskInfo> findWitkeyTaskInfo(DeviceInfo deviceInfo, SimCardInfo simCardInfo) throws LibException {
		if (deviceInfo == null || simCardInfo == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getFindWitkeyTaskInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("deviceInfo", gson.toJson(deviceInfo));
		httpRequestWrapper.addParameter("simCardInfo", gson.toJson(simCardInfo));
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<WitkeyTaskInfo> witkeyTaskInfoList = SocialCatJsonAdapter.createWitkeyTaskInfoList(
			response, gson);
		
		return witkeyTaskInfoList;
	}

	@Override
	public WitkeyTaskInfo getWitkeyTaskInfo(TaskType taskType) throws LibException {
		if (taskType == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, apiConf.getGetWitkeyTaskInfoURL(), auth);
		httpRequestWrapper.addParameter("clientVersion", clientVersion);
		httpRequestWrapper.addParameter("taskType", taskType.getTaskTypeNo());
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		WitkeyTaskInfo witkeyTaskInfo = SocialCatJsonAdapter.createWitkeyTaskInfo(response, gson);
		
		return witkeyTaskInfo;
	}
}

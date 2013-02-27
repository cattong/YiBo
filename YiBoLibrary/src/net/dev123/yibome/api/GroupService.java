package net.dev123.yibome.api;

import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.LocalGroup;
import net.dev123.yibome.entity.GroupSyncResult;
import net.dev123.yibome.entity.UserGroup;
import net.dev123.yibome.entity.UserGroupSyncResult;

public interface GroupService {
	
	GroupSyncResult syncGroups(List<LocalGroup> groups) throws LibException;
	
	UserGroupSyncResult syncGroupUsers(Long groupId, List<UserGroup> userGroups,
		ServiceProvider sp) throws LibException;
}

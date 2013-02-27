package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.User;

public interface GroupMethods {
	boolean createGroup(String groupName, String description)
			throws LibException;

	boolean createGroupMember(String userId, String groupId)
			throws LibException;

	List<User> getGroupMembers(String groupId, Paging<User> paging)
			throws LibException;

	boolean destroyGroup(String groupId) throws LibException;
}

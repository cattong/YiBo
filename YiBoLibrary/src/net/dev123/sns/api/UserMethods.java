package net.dev123.sns.api;

import java.util.List;

import net.dev123.exception.LibException;
import net.dev123.sns.entity.User;

public interface UserMethods {

	User showUser(String userId) throws LibException;

    List<User> showUsers(List<String> listUserId) throws LibException;

}

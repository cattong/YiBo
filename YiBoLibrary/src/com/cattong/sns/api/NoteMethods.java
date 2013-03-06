package com.cattong.sns.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.Note;


public interface NoteMethods {

	boolean createNote(String subject, String content,
			String... tags) throws LibException;

	boolean destroyNote(String noteId) throws LibException;

	Note showNote(String noteId, String ownerId) throws LibException;

	List<Note> getNotes(String ownerId, Paging<Note> paging) throws LibException;

}

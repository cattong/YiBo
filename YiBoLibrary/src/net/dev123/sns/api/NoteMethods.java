package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Note;
import net.dev123.sns.entity.Privacy;

public interface NoteMethods {

	boolean createNote(String subject, String content, Privacy privacy,
			String... tags) throws LibException;

	boolean destroyNote(String noteId) throws LibException;

	Note showNote(String noteId, String ownerId) throws LibException;

	List<Note> getNotes(String ownerId, Paging<Note> paging) throws LibException;

}

package com.cattong.sns;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.Note;

public class NoteMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void createNote() throws LibException {
		boolean result = sns.createNote("测试日志", "测试啊啊 啊");
		Assert.assertTrue(result);
	}

	@Test
	public void showNote() throws LibException {
		Paging<Note> paging = new Paging<Note>();
		paging.moveToFirst();
		List<Note> notes = sns.getNotes(sns.getUserId(), paging);
		if (notes != null && notes.size() > 0) {
			Note tmp = notes.get(0);
			Note note = sns.showNote(tmp.getId(), tmp.getUserId());
			junit.framework.Assert.assertTrue(note != null);
		} else {
			junit.framework.Assert.assertTrue(false);
		}
	}


	@Test
	public void getNotes() throws LibException {
		Paging<Note> paging = new Paging<Note>();
		paging.moveToFirst();
		List<Note> notes = sns.getNotes(sns.getUserId(), paging);
		Assert.assertTrue(notes != null && notes.size() > 0);
	}
}

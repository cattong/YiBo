package net.dev123.sns;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Note;
import net.dev123.sns.entity.Privacy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoteMethods {
	private static Sns sns = null;

	@BeforeClass
	public static void beforClass() throws LibException {
		sns = TokenConfig.getSns(TokenConfig.currentProvider);
	}

	@Test
	public void createNote() throws LibException {
		boolean result = sns.createNote("测试日志", "测试啊啊 啊", new Privacy());
		Assert.assertTrue(result);
	}

	@Test
	public void showNote() throws LibException {
		Paging<Note> paging = new Paging<Note>();
		paging.moveToFirst();
		List<Note> notes = sns.getNotes(sns.getUserId(), paging);
		if (notes != null && notes.size() > 0) {
			Note tmp = notes.get(0);
			Note note = sns.showNote(tmp.getId(), tmp.getFrom().getProfileId());
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

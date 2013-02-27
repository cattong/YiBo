package net.dev123.sns.entity;

public interface Profile {
	static enum ProfileType {
		USER, PAGE
	}

	String getProfileId();
	String getProfileName();
	String getProfilePicture();
	ProfileType getProfileType();
}

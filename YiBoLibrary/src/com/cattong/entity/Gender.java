package com.cattong.entity;

public enum Gender {
	Unkown(0), Male(1), Female(2);

	private int genderNo;

	private Gender(int genderNo) {
		this.genderNo = genderNo;
	}

	public int getGenderNo() {
		return genderNo;
	}

	public void setGenderNo(int genderNo) {
		this.genderNo = genderNo;
	}

	public static Gender getGender(int genderNo) {
		Gender gender = Gender.Unkown;
		switch (genderNo) {
		case 1:
			gender = Gender.Male;
			break;
		case 2:
			gender = Gender.Female;
			break;
		case 0:
		default:
			gender = Gender.Unkown;
			break;
		}

		return gender;
	}
}

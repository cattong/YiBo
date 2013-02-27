package net.dev123.entity;

public enum Gender {
	UNKNOW(0), MALE(1), FEMALE(2);

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
		Gender gender = Gender.UNKNOW;
		switch (genderNo) {
		case 1:
			gender = Gender.MALE;
			break;
		case 2:
			gender = Gender.FEMALE;
			break;
		case 0:
		default:
			gender = Gender.UNKNOW;
			break;
		}

		return gender;
	}
}

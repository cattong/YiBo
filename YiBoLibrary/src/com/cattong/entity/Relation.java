package com.cattong.entity;

public enum Relation {
    Noneship(0),         //source 与 target 没有关系
    Followingship(1),    //只有source following target;
    Followedship(2),     //只有source followed by target;
    Friendship(3);       //source following target, and source followed by target;
    
    private int type;
    private Relation(int type) {
    	this.type = type;
    }
    
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}

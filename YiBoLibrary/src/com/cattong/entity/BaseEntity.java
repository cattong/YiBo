package com.cattong.entity;

import java.io.Serializable;



/**
 * Base class for Model objects. Child objects should implement toString(),
 * equals() and hashCode().
 */
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -3248409460065397542L;

	/**
     * Returns a multi-line String with key=value pairs.
     * @return a String representation of this class.
     * 对象中存在互相引用时，需要重载
     */
    //public abstract String toString();

    /**
     * Compares object equality. When using Hibernate, the primary key should
     * not be a part of this comparison.
     * @param o object to compare to
     * @return true/false based on equality tests
     */
    public abstract boolean equals(Object o);

    /**
     * When you override equals, you should override hashCode. See "Why are
     * equals() and hashCode() importation" for more information:
     * http://www.hibernate.org/109.html
     * @return hashCode
     */
    public abstract int hashCode();
}

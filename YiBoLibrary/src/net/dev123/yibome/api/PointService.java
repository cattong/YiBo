package net.dev123.yibome.api;

import net.dev123.exception.LibException;
import net.dev123.yibome.entity.PointLevel;
import net.dev123.yibome.entity.PointOrderInfo;

public interface PointService {

	public PointLevel getPoints() throws LibException;
	
	public PointOrderInfo addLoginPoints() throws LibException;
}

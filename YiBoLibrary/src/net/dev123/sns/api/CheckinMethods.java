package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.entity.GeoLocation;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Checkin;

public interface CheckinMethods {

	boolean createPlace(String placeName, GeoLocation geoLocation)
			throws LibException;

	boolean checkin(String placeId, String message) throws LibException;

	boolean checkin(String placeName, GeoLocation geoLocation, String message,
			String... tags) throws LibException;

	Checkin showCheckin(String checkinId) throws LibException;

	List<Checkin> getCheckins(String profileId, Paging<Checkin> paging)
			throws LibException;
}

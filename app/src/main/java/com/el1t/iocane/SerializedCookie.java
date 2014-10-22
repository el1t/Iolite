package com.el1t.iocane;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by El1t on 10/21/14.
 */
public class SerializedCookie implements Serializable
{
	private String name;
	private String value;
	private String domain;
	private Date expiryDate;
	private String path;
	private int version;

	public SerializedCookie(Cookie cookie) {
		name = cookie.getName();
		value = cookie.getValue();
		domain = cookie.getDomain();
		path = cookie.getPath();
		expiryDate = cookie.getExpiryDate();
		version = cookie.getVersion();
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public int getVersion() {
		return version;
	}

	public BasicClientCookie toCookie() {
		BasicClientCookie out = new BasicClientCookie(name, value);
		out.setDomain(domain);
		out.setPath(path);
		out.setExpiryDate(expiryDate);
		out.setVersion(version);
		return out;
	}
}
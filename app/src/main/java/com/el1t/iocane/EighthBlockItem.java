package com.el1t.iocane;

import java.io.Serializable;

/**
 * Created by El1t on 10/24/14.
 */
public class EighthBlockItem implements Serializable
{
	private EighthActivityItem activity;
	private String date;
	private int BID;
	private String type;
	private boolean locked;
	private String disp;

	public EighthBlockItem(EighthActivityItem activity, String date, int BID, String type, boolean locked, String disp) {
		this.activity = activity;
		this.date = date;
		this.BID = BID;
		this.type = type;
		this.locked = locked;
		this.disp = disp;
	}

	public EighthActivityItem getEighth() {
		return activity;
	}

	public String getDate() {
		return date;
	}

	public int getBID() {
		return BID;
	}

	public String getType() {
		return type;
	}

	public boolean isLocked() {
		return locked;
	}

	public String getDisp() {
		return disp;
	}
}
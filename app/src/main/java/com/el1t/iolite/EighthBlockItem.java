package com.el1t.iolite;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class EighthBlockItem implements Serializable
{
	private EighthActivityItem activity;
	private Date date;
	private int BID;
	private String type;
	private boolean locked;
	private String disp;

	public EighthBlockItem(EighthActivityItem activity, Date date, int BID, String type, boolean locked, String disp) {
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

	public Date getDate() {
		return date;
	}

	public int getBID() {
		return BID;
	}

	public String getBlock() {
		return type;
	}

	public boolean isLocked() {
		return locked;
	}

	public String getDisp() {
		return disp;
	}
}
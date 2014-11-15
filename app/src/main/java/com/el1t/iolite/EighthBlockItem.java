package com.el1t.iolite;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	private DateFormat mDateFormat = new SimpleDateFormat("EEEE, MMMM d");
	private boolean header;

	public EighthBlockItem(EighthActivityItem activity, Date date, int BID, String type, boolean locked, String disp) {
		this.activity = activity;
		this.date = date;
		this.BID = BID;
		this.type = type;
		this.locked = locked;
		this.disp = disp;
	}

	// Constructor for a header item
	public EighthBlockItem(Date date) {
		header = true;
		this.date = date;
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

	public boolean isHeader() {
		return header;
	}

	public Spannable getShortenedDisp() {
		// Add postfix
		String str = mDateFormat.format(date);
		switch(str.charAt(str.length() - 1)) {
			case '1':
				str += "st";
				break;
			case '2':
				str += "nd";
				break;
			case '3':
				str += "rd";
				break;
			default:
				str += "th";
		}
		// Format postfix into superscript
		Spannable sp = new SpannableString(str);
		sp.setSpan(new SuperscriptSpan(), sp.length() - 2, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(.75f), sp.length() - 2, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sp;
	}
}
package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by El1t on 10/24/14.
 */
public class EighthBlockItem implements Parcelable
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM d");
	private EighthActivityItem activity;
	private Date date;
	private int BID;
	private String type;
	private boolean locked;
	private boolean header;

	public EighthBlockItem() {
		activity = new EighthActivityItem();
		date = new Date();
		type = "";
	}

	// Constructor for a header item
	public EighthBlockItem(Date date) {
		header = true;
		this.date = date;
		type = "";
	}

	public EighthActivityItem getEighth() {
		return activity;
	}

	// No setter for ActivityItem - use setters for inner item

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getBID() {
		return BID;
	}

	public void setBID(int BID) {
		this.BID = BID;
	}

	public String getBlock() {
		return type;
	}

	public void setBlock(String type) {
		this.type = type;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isHeader() {
		return header;
	}

	public Spannable getDisp() {
		// Add postfix
		String str = DATE_FORMAT.format(date);
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

	protected EighthBlockItem(Parcel in) {
		activity = in.readParcelable(EighthActivityItem.class.getClassLoader());
		long tmpDate = in.readLong();
		date = tmpDate != -1 ? new Date(tmpDate) : null;
		BID = in.readInt();
		type = in.readString();
		locked = in.readByte() != 0;
		header = in.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(activity, flags);
		dest.writeLong(date != null ? date.getTime() : -1L);
		dest.writeInt(BID);
		dest.writeString(type);
		dest.writeByte((byte) (locked ? 1 : 0));
		dest.writeByte((byte) (header ? 1 : 0));
	}

	public static final Parcelable.Creator<EighthBlockItem> CREATOR = new Parcelable.Creator<EighthBlockItem>() {
		@Override
		public EighthBlockItem createFromParcel(Parcel in) {
			return new EighthBlockItem(in);
		}

		@Override
		public EighthBlockItem[] newArray(int size) {
			return new EighthBlockItem[size];
		}
	};
}
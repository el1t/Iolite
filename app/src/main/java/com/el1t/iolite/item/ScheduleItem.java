package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by El1t on 12/11/14.
 */
public class ScheduleItem implements Parcelable, Comparable<ScheduleItem>
{
	private int block;
	private String name;
	private String times;
	private boolean header;

	public ScheduleItem(int block, String name, String times) {
		this.block = block;
		this.name = name;
		this.times = times;
		header = false;
	}

	// Create header
	public ScheduleItem(String day, String type) {
		block = -1;
		this.name = day;
		this.times = type;
		header = true;
	}

	public String getName() {
		return name;
	}

	public String getTimes() {
		return times;
	}

	public boolean isHeader() {
		return header;
	}

	public int compareTo(ScheduleItem item) {
		return block - item.block;
	}

	protected ScheduleItem(Parcel in) {
		block = in.readInt();
		name = in.readString();
		times = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(block);
		dest.writeString(name);
		dest.writeString(times);
	}

	public static final Parcelable.Creator<ScheduleItem> CREATOR = new Parcelable.Creator<ScheduleItem>() {
		@Override
		public ScheduleItem createFromParcel(Parcel in) {
			return new ScheduleItem(in);
		}

		@Override
		public ScheduleItem[] newArray(int size) {
			return new ScheduleItem[size];
		}
	};
}
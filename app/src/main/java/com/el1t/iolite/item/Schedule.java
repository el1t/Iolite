package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by El1t on 12/11/14.
 */
public class Schedule implements Parcelable
{
	private String day;
	private String type;
	private String yesterday;
	private String tomorrow;
	private ArrayList<ScheduleItem> items;

	/**
	 * @param day Date (e.g. Monday, February 29)
	 * @param type Day color (e.g. Blue)
	 * @param yesterday Date code for previous day (e.g. 20150101)
	 * @param tomorrow Date code for next day (e.g. 20150103)
	 * @param items Schedule items
	 */
	public Schedule(String day, String type, String yesterday, String tomorrow, ArrayList<ScheduleItem> items) {
		this.day = day;
		this.type = type;
		this.yesterday = yesterday;
		this.tomorrow = tomorrow;
		this.items = items;
	}

	public static class ScheduleBuilder {
		private String day;
		private String type;
		private String yesterday;
		private String tomorrow;
		private ArrayList<ScheduleItem> items;

		public ScheduleBuilder day(String day) {
			this.day = day;
			return this;
		}

		public ScheduleBuilder type(String type) {
			this.type = type;
			return this;
		}

		public ScheduleBuilder yesterday(String yesterday) {
			this.yesterday = yesterday;
			return this;
		}

		public ScheduleBuilder tomorrow(String tomorrow) {
			this.tomorrow = tomorrow;
			return this;
		}

		public ScheduleBuilder items(ArrayList<ScheduleItem> items) {
			this.items = items;
			return this;
		}

		public Schedule build() {
			return new Schedule(day, type, yesterday, tomorrow, items);
		}
	}

	public String getDay() {
		return day;
	}

	public String getType() {
		return type;
	}

	public String getYesterday() {
		return yesterday;
	}

	public String getTomorrow() {
		return tomorrow;
	}

	public ArrayList<ScheduleItem> getItems() {
		return items;
	}

	protected Schedule(Parcel in) {
		day = in.readString();
		type = in.readString();
		yesterday = in.readString();
		tomorrow = in.readString();
		if (in.readByte() == 1) {
			items = new ArrayList<>();
			in.readTypedList(items, ScheduleItem.CREATOR);
		} else {
			items = null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(day);
		dest.writeString(type);
		dest.writeString(yesterday);
		dest.writeString(tomorrow);
		if (items == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeList(items);
		}
	}

	public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
		@Override
		public Schedule createFromParcel(Parcel in) {
			return new Schedule(in);
		}

		@Override
		public Schedule[] newArray(int size) {
			return new Schedule[size];
		}
	};
}

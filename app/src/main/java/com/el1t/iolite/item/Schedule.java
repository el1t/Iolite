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
	private String blocks;
	private String times;

	/**
	 * @param day Date (e.g. Monday, February 29)
	 * @param type Day color (e.g. Blue)
	 * @param yesterday Date code for previous day (e.g. 20150101)
	 * @param tomorrow Date code for next day (e.g. 20150103)
	 * @param blocks Names of the blocks
	 * @param times Time intervals for blocks
	 */
	public Schedule(String day, String type, String yesterday, String tomorrow, String blocks, String times) {
		this.day = day;
		this.type = type;
		this.yesterday = yesterday;
		this.tomorrow = tomorrow;
		this.blocks = blocks;
		this.times = times;
	}

	public static class ScheduleBuilder {
		private String day;
		private String type;
		private String yesterday;
		private String tomorrow;
		private String blocks;
		private String times;

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

		public ScheduleBuilder blocks(String blocks) {
			this.blocks = blocks;
			return this;
		}

		public ScheduleBuilder times(String times) {
			this.times = times;
			return this;
		}

		public Schedule build() {
			return new Schedule(day, type, yesterday, tomorrow, blocks, times);
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

	public String getBlocks() {
		return blocks;
	}

	public String getTimes() {
		return times;
	}

	protected Schedule(Parcel in) {
		day = in.readString();
		type = in.readString();
		yesterday = in.readString();
		tomorrow = in.readString();
		blocks = in.readString();
		times = in.readString();
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
		dest.writeString(blocks);
		dest.writeString(times);
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

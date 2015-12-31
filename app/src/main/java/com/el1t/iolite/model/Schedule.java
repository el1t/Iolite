package com.el1t.iolite.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by El1t on 12/11/14.
 */
public class Schedule implements Parcelable, Comparable<Schedule> {
	private String day;
	private Date date;
	private String type;
	private String blocks;
	private String times;

	/**
	 * @param day       Date (e.g. Monday, February 29)
	 * @param type      Day color (e.g. Blue)
	 * @param blocks    Names of the blocks
	 * @param times     Time intervals for blocks
	 */
	public Schedule(String day, Date date, String type, String blocks, String times) {
		this.day = day;
		this.date = date;
		this.type = type;
		this.blocks = blocks;
		this.times = times;
	}

	public static class Builder {
		private String day;
		private Date date;
		private String type;
		private String blocks;
		private String times;

		public Builder day(String day) {
			this.day = day;
			return this;
		}

		public Builder date(Date date) {
			this.date = date;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder blocks(String blocks) {
			this.blocks = blocks;
			return this;
		}

		public Builder times(String times) {
			this.times = times;
			return this;
		}

		public Schedule build() {
			return new Schedule(day, date, type, blocks, times);
		}
	}

	public String getDay() {
		return day;
	}

	public Date getDate() {
		return date;
	}

	public String getType() {
		return type;
	}

	public String getBlocks() {
		return blocks;
	}

	public String getTimes() {
		return times;
	}

	@Override
	public int compareTo(@NonNull Schedule schedule) {
		return date.compareTo(schedule.date);
	}

	protected Schedule(Parcel in) {
		day = in.readString();
		date = new Date(in.readLong());
		type = in.readString();
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
		dest.writeLong(date.getTime());
		dest.writeString(type);
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

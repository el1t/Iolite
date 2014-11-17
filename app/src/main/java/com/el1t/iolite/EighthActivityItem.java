package com.el1t.iolite;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivityItem implements Serializable, Parcelable
{
	private int AID;
	private String name;
	private String description;
	private boolean restricted;
	private boolean presign;
	private boolean oneaday;
	private boolean bothblocks;
	private boolean sticky;
	private boolean special;
	private boolean calendar;
	private boolean roomChanged;
	private String blockRoomString;
	private int BID;
	private boolean cancelled;
	private boolean attendanceTaken;
	private boolean favorite;
	private int memberCount;
	private int capacity;
	private String firstChar;

	private boolean header;

	public EighthActivityItem() {
		// Initialize strings
		// Primitive types are automatically initialized
		name = "";
		description = "";
		blockRoomString = "";
		firstChar = "";
	}

	// Create a header object
	public EighthActivityItem(String name, ActivityListAdapter.ActivityHeaderType headerType) {
		this.header = true;
		this.name = name;
		switch(headerType) {
			case FAVORITE:
				favorite = true;
				break;
			case SPECIAL:
				special = true;
				break;
			case GENERAL:
			default:
				break;
		}
	}

	private void firstCharHelper() {
		for(char c : name.toCharArray()) {
			if(Character.isLetter(c)) {
				firstChar = String.valueOf(Character.toUpperCase(c));
				return;
			}
		}
		firstChar = name.substring(0, 1).toUpperCase();
	}

	public int getAID() {
		return AID;
	}

	public void setAID(int AID) {
		this.AID = AID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		firstCharHelper();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.trim();
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isPresign() {
		return presign;
	}

	public void setPresign(boolean presign) {
		this.presign = presign;
	}

	public boolean isOneaday() {
		return oneaday;
	}

	public void setOneaday(boolean oneaday) {
		this.oneaday = oneaday;
	}

	public boolean isBothblocks() {
		return bothblocks;
	}

	public void setBothblocks(boolean bothblocks) {
		this.bothblocks = bothblocks;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public boolean isSpecial() {
		return special;
	}

	public void setSpecial(boolean special) {
		this.special = special;
	}

	public boolean isCalendar() {
		return calendar;
	}

	public void setCalendar(boolean calendar) {
		this.calendar = calendar;
	}

	public boolean isRoomChanged() {
		return roomChanged;
	}

	public void setRoomChanged(boolean roomChanged) {
		this.roomChanged = roomChanged;
	}

	public String getBlockRoomString() {
		return blockRoomString;
	}

	public void setBlockRoomString(String blockRoomString) {
		this.blockRoomString = blockRoomString;
	}

	public int getBID() {
		return BID;
	}

	public void setBID(int BID) {
		this.BID = BID;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isAttendanceTaken() {
		return attendanceTaken;
	}

	public void setAttendanceTaken(boolean attendanceTaken) {
		this.attendanceTaken = attendanceTaken;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getFirstChar() {
		return firstChar;
	}

	public boolean isHeader() {
		return header;
	}

	public boolean hasDescription() {
		return description.equals("") || description.trim().toLowerCase().equals("no description available");
	}

	public boolean isFull() {
		return memberCount >= capacity;
	}

	public boolean changeFavorite() {
		return favorite = !favorite;
	}

	protected EighthActivityItem(Parcel in) {
		AID = in.readInt();
		name = in.readString();
		description = in.readString();
		restricted = in.readByte() != 0x00;
		presign = in.readByte() != 0x00;
		oneaday = in.readByte() != 0x00;
		bothblocks = in.readByte() != 0x00;
		sticky = in.readByte() != 0x00;
		special = in.readByte() != 0x00;
		calendar = in.readByte() != 0x00;
		roomChanged = in.readByte() != 0x00;
		blockRoomString = in.readString();
		BID = in.readInt();
		cancelled = in.readByte() != 0x00;
		attendanceTaken = in.readByte() != 0x00;
		favorite = in.readByte() != 0x00;
		memberCount = in.readInt();
		capacity = in.readInt();
		firstChar = in.readString();
		header = in.readByte() != 0x00;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(AID);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeByte((byte) (restricted ? 0x01 : 0x00));
		dest.writeByte((byte) (presign ? 0x01 : 0x00));
		dest.writeByte((byte) (oneaday ? 0x01 : 0x00));
		dest.writeByte((byte) (bothblocks ? 0x01 : 0x00));
		dest.writeByte((byte) (sticky ? 0x01 : 0x00));
		dest.writeByte((byte) (special ? 0x01 : 0x00));
		dest.writeByte((byte) (calendar ? 0x01 : 0x00));
		dest.writeByte((byte) (roomChanged ? 0x01 : 0x00));
		dest.writeString(blockRoomString);
		dest.writeInt(BID);
		dest.writeByte((byte) (cancelled ? 0x01 : 0x00));
		dest.writeByte((byte) (attendanceTaken ? 0x01 : 0x00));
		dest.writeByte((byte) (favorite ? 0x01 : 0x00));
		dest.writeInt(memberCount);
		dest.writeInt(capacity);
		dest.writeString(firstChar);
		dest.writeByte((byte) (header ? 0x01 : 0x00));
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<EighthActivityItem> CREATOR = new Parcelable.Creator<EighthActivityItem>() {
		@Override
		public EighthActivityItem createFromParcel(Parcel in) {
			return new EighthActivityItem(in);
		}

		@Override
		public EighthActivityItem[] newArray(int size) {
			return new EighthActivityItem[size];
		}
	};
}
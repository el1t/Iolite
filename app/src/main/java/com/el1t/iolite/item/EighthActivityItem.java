package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.el1t.iolite.adapter.SignupListAdapter;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivityItem implements Parcelable
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
	private String sponsors;
	private String room;
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
		room = "";
		firstChar = "";
	}

	// Create a header object
	public EighthActivityItem(String name, SignupListAdapter.ActivityHeaderType headerType) {
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

	// Reject empty descriptions
	public void setDescription(String description) {
		if (!description.equals("") && !description.toLowerCase().equals("no description available")) {
			this.description = description;
		}
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

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getSponsors() {
		return sponsors;
	}

	public void setSponsors(String sponsors) {
		this.sponsors = sponsors;
	}

	public boolean hasSponsors() {
		return !(sponsors.equals("") || sponsors.equals("CANCELLED"));
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
		return !description.equals("");
	}

	public boolean isFull() {
		return capacity > 0 && memberCount >= capacity;
	}

	public boolean changeFavorite() {
		return favorite = !favorite;
	}

	protected EighthActivityItem(Parcel in) {
		AID = in.readInt();
		name = in.readString();
		description = in.readString();
		restricted = in.readByte() != 0;
		presign = in.readByte() != 0;
		oneaday = in.readByte() != 0;
		bothblocks = in.readByte() != 0;
		sticky = in.readByte() != 0;
		special = in.readByte() != 0;
		calendar = in.readByte() != 0;
		roomChanged = in.readByte() != 0;
		room = in.readString();
		BID = in.readInt();
		cancelled = in.readByte() != 0;
		attendanceTaken = in.readByte() != 0;
		favorite = in.readByte() != 0;
		memberCount = in.readInt();
		capacity = in.readInt();
		firstChar = in.readString();
		header = in.readByte() != 0;
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
		dest.writeByte((byte) (restricted ? 1 : 0));
		dest.writeByte((byte) (presign ? 1 : 0));
		dest.writeByte((byte) (oneaday ? 1 : 0));
		dest.writeByte((byte) (bothblocks ? 1 : 0));
		dest.writeByte((byte) (sticky ? 1 : 0));
		dest.writeByte((byte) (special ? 1 : 0));
		dest.writeByte((byte) (calendar ? 1 : 0));
		dest.writeByte((byte) (roomChanged ? 1 : 0));
		dest.writeString(room);
		dest.writeInt(BID);
		dest.writeByte((byte) (cancelled ? 1 : 0));
		dest.writeByte((byte) (attendanceTaken ? 1 : 0));
		dest.writeByte((byte) (favorite ? 1 : 0));
		dest.writeInt(memberCount);
		dest.writeInt(capacity);
		dest.writeString(firstChar);
		dest.writeByte((byte) (header ? 1 : 0));
	}

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
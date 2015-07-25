package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.el1t.iolite.Utils;
import com.el1t.iolite.adapter.SignupListAdapter;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivityItem implements Parcelable
{
	private int AID;
	private int BID;
	private int memberCount;
	private int capacity;
	private String name;
	private String description;
	private String URL;
	private String firstChar;
	private String[] sponsors;
	private String[] rooms;
	private boolean restricted;
	private boolean administrative;
	private boolean presign;
	private boolean bothblocks;
	private boolean sticky;
	private boolean special;
	private boolean cancelled;
	private boolean favorite;

	private boolean header;

	public EighthActivityItem(int AID, int BID, int memberCount, int capacity, String name,
	                          String description, String URL, String[] sponsors,
	                          String[] rooms, boolean restricted, boolean administrative,
	                          boolean presign, boolean bothblocks, boolean sticky, boolean special,
	                          boolean cancelled, boolean favorite) {
		firstChar = name.substring(0, 1).toUpperCase();
		this.AID = AID;
		this.BID = BID;
		this.memberCount = memberCount;
		this.capacity = capacity;
		this.name = name;
		this.description = description;
		this.URL = URL;
		this.sponsors = sponsors;
		this.rooms = rooms;
		this.restricted = restricted;
		this.administrative = administrative;
		this.presign = presign;
		this.bothblocks = bothblocks;
		this.sticky = sticky;
		this.special = special;
		this.cancelled = cancelled;
		this.favorite = favorite;
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

	public static class EighthActivityItemBuilder {
		private int AID;
		private int BID;
		private int memberCount;
		private int capacity;
		private String name;
		private String description;
		private String URL;
		private String[] sponsors;
		private String[] rooms;
		private boolean restricted;
		private boolean administrative;
		private boolean presign;
		private boolean bothblocks;
		private boolean sticky;
		private boolean special;
		private boolean cancelled;
		private boolean favorite;

		public EighthActivityItemBuilder AID(int AID) {
			this.AID = AID;
			return this;
		}

		public EighthActivityItemBuilder BID(int BID) {
			this.BID = BID;
			return this;
		}

		public EighthActivityItemBuilder memberCount(int memberCount) {
			this.memberCount = memberCount;
			return this;
		}

		public EighthActivityItemBuilder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public EighthActivityItemBuilder name(String name) {
			this.name = name.trim();
			return this;
		}

		public EighthActivityItemBuilder description(String description) {
			if (description.toLowerCase().equals("no description available")) {
				this.description = "";
			} else {
				this.description = description.trim();
			}
			return this;
		}

		public EighthActivityItemBuilder URL(String URL) {
			this.URL = URL;
			return this;
		}

		public EighthActivityItemBuilder sponsors(String[] sponsors) {
			this.sponsors = sponsors;
			return this;
		}

		public EighthActivityItemBuilder rooms(String[] rooms) {
			this.rooms = rooms;
			return this;
		}

		public EighthActivityItemBuilder administrative(boolean administrative) {
			this.administrative = administrative;
			return this;
		}

		public EighthActivityItemBuilder restricted(boolean restricted) {
			this.restricted = restricted;
			return this;
		}

		public EighthActivityItemBuilder presign(boolean presign) {
			this.presign = presign;
			return this;
		}

		public EighthActivityItemBuilder bothblocks(boolean bothblocks) {
			this.bothblocks = bothblocks;
			return this;
		}

		public EighthActivityItemBuilder sticky(boolean sticky) {
			this.sticky = sticky;
			return this;
		}

		public EighthActivityItemBuilder special(boolean special) {
			this.special = special;
			return this;
		}

		public EighthActivityItemBuilder cancelled(boolean cancelled) {
			this.cancelled = cancelled;
			return this;
		}

		public EighthActivityItemBuilder favorite(boolean favorite) {
			this.favorite = favorite;
			return this;
		}

		public EighthActivityItem build() {
			return new EighthActivityItem(AID, BID, memberCount, capacity, name, description, URL,
					sponsors, rooms, restricted, administrative, presign, bothblocks, sticky,
					special, cancelled, favorite);

		}
	}

	public int getAID() {
		return AID;
	}

	public String getName() {
		return name;
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

	public boolean isPresign() {
		return presign;
	}

	public boolean isBothblocks() {
		return bothblocks;
	}

	public boolean isSticky() {
		return sticky;
	}

	public boolean isSpecial() {
		return special;
	}

	public String[] getRoomsArray() {
		return rooms;
	}

	public String getRooms() {
		return Utils.join(rooms);
	}

	public String[] getSponsorsArray() {
		return sponsors;
	}

	public String getSponsors() {
		return Utils.join(sponsors);
	}

	public boolean hasSponsors() {
		return !(sponsors.length == 0 || sponsors[0].equals("CANCELLED"));
	}

	public int getBID() {
		return BID;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getFirstChar() {
		return firstChar;
	}

	public boolean isHeader() {
		return header;
	}

	public boolean hasDescription() {
		return !description.isEmpty();
	}

	public boolean isFull() {
		return capacity > 0 && memberCount >= capacity;
	}

	public boolean changeFavorite() {
		return favorite = !favorite;
	}

	protected EighthActivityItem(Parcel in) {
		AID = in.readInt();
		BID = in.readInt();
		memberCount = in.readInt();
		capacity = in.readInt();
		name = in.readString();
		description = in.readString();
		URL = in.readString();
		firstChar = in.readString();
		sponsors = in.createStringArray();
		rooms = in.createStringArray();
		restricted = in.readByte() != 0;
		administrative = in.readByte() != 0;
		presign = in.readByte() != 0;
		bothblocks = in.readByte() != 0;
		sticky = in.readByte() != 0;
		special = in.readByte() != 0;
		cancelled = in.readByte() != 0;
		favorite = in.readByte() != 0;
		header = in.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(AID);
		dest.writeInt(BID);
		dest.writeInt(memberCount);
		dest.writeInt(capacity);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(URL);
		dest.writeString(firstChar);
		dest.writeStringArray(sponsors);
		dest.writeStringArray(rooms);
		dest.writeByte((byte) (restricted ? 1 : 0));
		dest.writeByte((byte) (administrative ? 1 : 0));
		dest.writeByte((byte) (presign ? 1 : 0));
		dest.writeByte((byte) (bothblocks ? 1 : 0));
		dest.writeByte((byte) (sticky ? 1 : 0));
		dest.writeByte((byte) (special ? 1 : 0));
		dest.writeByte((byte) (cancelled ? 1 : 0));
		dest.writeByte((byte) (favorite ? 1 : 0));
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
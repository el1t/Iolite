package com.el1t.iolite.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.el1t.iolite.adapter.SignupAdapter;
import com.el1t.iolite.util.Utils;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivity implements Parcelable, Comparable<EighthActivity> {
	private int AID;
	private int BID;
	private int SID;
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

	private boolean header;

	public EighthActivity(int AID, int BID, int SID, int memberCount, int capacity, String name,
	                      String description, String URL, String[] sponsors, String[] rooms,
	                      boolean restricted, boolean administrative, boolean presign,
	                      boolean bothblocks, boolean sticky, boolean special, boolean cancelled,
	                      boolean favorite) {
		this.AID = AID;
		this.BID = BID;
		this.SID = SID;
		this.memberCount = memberCount;
		this.capacity = capacity;
		this.name = name;
		if (description == null || description.isEmpty()) {
			this.description = "No description.";
		} else {
			this.description = description;
		}
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
	public EighthActivity(String name, SignupAdapter.ActivityHeaderType headerType) {
		this.header = true;
		this.name = name;
		switch (headerType) {
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

	public static class Builder {
		private int AID;
		private int BID;
		private int SID;
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
		private EighthActivity activity;

		public Builder() {}

		public Builder(EighthActivity activity) {
			this.activity = activity;
		}

		public Builder AID(int AID) {
			this.AID = AID;
			return this;
		}

		public Builder BID(int BID) {
			this.BID = BID;
			return this;
		}

		public Builder SID(int SID) {
			this.SID = SID;
			return this;
		}

		public Builder memberCount(int memberCount) {
			this.memberCount = memberCount;
			return this;
		}

		public Builder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public Builder name(String name) {
			this.name = name.trim();
			return this;
		}

		public Builder description(String description) {
			if (description.toLowerCase().equals("no description available")) {
				this.description = null;
			} else {
				this.description = description.trim();
			}
			return this;
		}

		public Builder URL(String URL) {
			this.URL = URL;
			return this;
		}

		public Builder sponsors(String[] sponsors) {
			this.sponsors = sponsors;
			return this;
		}

		public Builder rooms(String[] rooms) {
			this.rooms = rooms;
			return this;
		}

		public Builder administrative(boolean administrative) {
			this.administrative = administrative;
			return this;
		}

		public Builder restricted(boolean restricted) {
			this.restricted = restricted;
			return this;
		}

		public Builder presign(boolean presign) {
			this.presign = presign;
			return this;
		}

		public Builder bothblocks(boolean bothblocks) {
			this.bothblocks = bothblocks;
			return this;
		}

		public Builder sticky(boolean sticky) {
			this.sticky = sticky;
			return this;
		}

		public Builder special(boolean special) {
			this.special = special;
			return this;
		}

		public Builder cancelled(boolean cancelled) {
			this.cancelled = cancelled;
			return this;
		}

		public Builder favorite(boolean favorite) {
			this.favorite = favorite;
			return this;
		}

		public EighthActivity build() {
			if (activity == null) {
				return new EighthActivity(AID, BID, SID, memberCount, capacity, name, description, URL,
						sponsors, rooms, restricted, administrative, presign, bothblocks, sticky,
						special, cancelled, favorite);
			}
			if (this.administrative) {
				activity.administrative = this.administrative;
			}
			if (this.restricted) {
				activity.restricted = this.restricted;
			}
			if (this.presign) {
				activity.presign = this.presign;
			}
			if (this.bothblocks) {
				activity.bothblocks = this.bothblocks;
			}
			if (this.sticky) {
				activity.sticky = this.sticky;
			}
			if (this.special) {
				activity.special = this.special;
			}
			if (this.cancelled) {
				activity.cancelled = this.cancelled;
			}
			if (this.favorite) {
				activity.favorite = this.favorite;
			}
			if (this.description != null) {
				activity.description = this.description;
			}
			return activity;
		}
	}

	/**
	 * Check if activity has been selected
	 * @return true if activity is not selected
	 */
	public boolean isNull() {
		return AID == -1;
	}

	public int getAID() {
		return AID;
	}

	public int getBID() {
		return BID;
	}

	public int getSID() {
		return SID;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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

	public String getRoomsNoDelim() {
		return Utils.join(rooms, "");
	}

	public boolean hasRooms() {
		return rooms != null && rooms.length > 0;
	}

	public String[] getSponsorsArray() {
		return sponsors;
	}

	public String getSponsors() {
		return Utils.join(sponsors);
	}

	public String getSponsorsNoDelim() {
		return Utils.join(sponsors, "");
	}

	public boolean hasSponsors() {
		return !(sponsors == null || sponsors.length == 0 || sponsors[0].equals("CANCELLED"));
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

	public boolean isHeader() {
		return header;
	}

	public boolean hasDescription() {
		return !(description == null || description.equals("No description."));
	}

	public boolean isFull() {
		return capacity > 0 && memberCount >= capacity;
	}

	public boolean changeFavorite() {
		return favorite = !favorite;
	}

	public int compareTo(@NonNull EighthActivity other) {
		// Compare by name if both or neither are favorites, or return the favorite
		if (favorite) {
			if (other.favorite) {
				return name.compareToIgnoreCase(other.name);
			}
			return -1;
		}
		if (other.favorite) {
			return 1;
		}

		// Check for special
		if (!(special ^ other.special)) {
			return name.compareToIgnoreCase(other.name);
		}
		if (special) {
			return -1;
		}
		return 1;
	}

	protected EighthActivity(Parcel in) {
		AID = in.readInt();
		BID = in.readInt();
		SID = in.readInt();
		memberCount = in.readInt();
		capacity = in.readInt();
		name = in.readString();
		description = in.readString();
		URL = in.readString();
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
		dest.writeInt(SID);
		dest.writeInt(memberCount);
		dest.writeInt(capacity);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(URL);
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

	public static final Parcelable.Creator<EighthActivity> CREATOR = new Parcelable.Creator<EighthActivity>() {
		@Override
		public EighthActivity createFromParcel(Parcel in) {
			return new EighthActivity(in);
		}

		@Override
		public EighthActivity[] newArray(int size) {
			return new EighthActivity[size];
		}
	};
}
package com.el1t.iolite.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.el1t.iolite.adapter.DetailCardAdapter.Types;

import java.util.ArrayList;

/**
 * Created by El1t on 7/25/15.
 */
public class Detail implements Parcelable
{
	private Types type;
	private int AID;
	private int BID;
	private int memberCount;
	private int capacity;
	private String description;
	private String[] sponsors;
	private String[] rooms;
	private boolean administrative;
	private boolean restricted;
	private boolean presign;
	private boolean bothblocks;
	private boolean sticky;
	private boolean special;
	private boolean cancelled;
	private boolean favorite;

	public Detail(boolean administrative, boolean restricted, boolean presign, boolean bothblocks,
	              boolean sticky, boolean special, boolean cancelled, boolean favorite) {
		this.type = Types.STATUS;
		this.administrative = administrative;
		this.restricted = restricted;
		this.presign = presign;
		this.bothblocks = bothblocks;
		this.sticky = sticky;
		this.special = special;
		this.cancelled = cancelled;
		this.favorite = favorite;
	}

	public Detail(String description) {
		this.type = Types.DESCRIPTION;
		this.description = description;
	}

	public static Detail[] fromActivity(EighthActivityItem activity) {
		return new DetailBuilder()
//				.AID(activity.getAID())
				.memberCount(activity.getMemberCount())
				.capacity(activity.getCapacity())
				.description(activity.getDescription())
				.sponsors(activity.getSponsorsArray())
				.rooms(activity.getRoomsArray())
				.restricted(activity.isRestricted())
				.presign(activity.isPresign())
				.bothblocks(activity.isBothblocks())
				.sticky(activity.isSticky())
				.special(activity.isSpecial())
				.cancelled(activity.isCancelled())
//				.favorite(activity.isFavorite())
				.build();
	}

	public static class DetailBuilder {
		private int AID;
		private int BID;
		private int memberCount;
		private int capacity;
		private String description;
		private String[] sponsors;
		private String[] rooms;
		private boolean administrative;
		private boolean restricted;
		private boolean presign;
		private boolean bothblocks;
		private boolean sticky;
		private boolean special;
		private boolean cancelled;
		private boolean favorite;
		private ArrayList<Detail> details;

		public DetailBuilder() { }

		public DetailBuilder(ArrayList<Detail> details) {
			this.details = details;
		}

		public DetailBuilder AID(int AID) {
			this.AID = AID;
			return this;
		}

		public DetailBuilder memberCount(int memberCount) {
			this.memberCount = memberCount;
			return this;
		}

		public DetailBuilder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public DetailBuilder description(String description) {
			this.description = description;
			return this;
		}

		public DetailBuilder sponsors(String[] sponsors) {
			this.sponsors = sponsors;
			return this;
		}

		public DetailBuilder rooms(String[] rooms) {
			this.rooms = rooms;
			return this;
		}

		public DetailBuilder administrative(boolean administrative) {
			this.administrative = administrative;
			return this;
		}

		public DetailBuilder restricted(boolean restricted) {
			this.restricted = restricted;
			return this;
		}

		public DetailBuilder presign(boolean presign) {
			this.presign = presign;
			return this;
		}

		public DetailBuilder bothblocks(boolean bothblocks) {
			this.bothblocks = bothblocks;
			return this;
		}

		public DetailBuilder sticky(boolean sticky) {
			this.sticky = sticky;
			return this;
		}

		public DetailBuilder special(boolean special) {
			this.special = special;
			return this;
		}

		public DetailBuilder cancelled(boolean cancelled) {
			this.cancelled = cancelled;
			return this;
		}

		public DetailBuilder favorite(boolean favorite) {
			this.favorite = favorite;
			return this;
		}

		public Detail[] build() {
			if (details == null) {
				final Detail[] details = new Detail[3];
				details[0] = new Detail(administrative, restricted, presign, bothblocks, sticky, special, cancelled, favorite);
				details[1] = new Detail(description);
				return details;
			} else {
				if (!details.get(0).administrative) {
					details.get(0).administrative = administrative;
				}
				if (!details.get(0).restricted) {
					details.get(0).restricted = restricted;
				}
				if (!details.get(0).presign) {
					details.get(0).presign = presign;
				}
				if (!details.get(0).bothblocks) {
					details.get(0).bothblocks = bothblocks;
				}
				if (!details.get(0).sticky) {
					details.get(0).sticky = sticky;
				}
				if (!details.get(0).special) {
					details.get(0).special = special;
				}
				if (!details.get(0).cancelled) {
					details.get(0).cancelled = cancelled;
				}
				if (!details.get(0).favorite) {
					details.get(0).favorite = favorite;
				}
				if (description != null) {
					details.get(1).description = description;
				}
				return null;
			}
		}
	}

	public String getTitle() {
		return type.toString();
	}

	public Types getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	protected Detail(Parcel in) {
		type = Types.values()[in.readInt()];
		switch(type) {
			case DESCRIPTION:
				description = in.readString();
				break;
			case STATUS:
				this.restricted = in.readByte() != 0;
				this.presign = in.readByte() != 0;
				this.bothblocks = in.readByte() != 0;
				this.sticky = in.readByte() != 0;
				this.special = in.readByte() != 0;
				this.cancelled = in.readByte() != 0;
				this.favorite = in.readByte() != 0;
				break;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type.ordinal());
		switch(type) {
			case DESCRIPTION:
				dest.writeString(description);
			case STATUS:
				dest.writeByte((byte) (restricted ? 1 : 0));
				dest.writeByte((byte) (presign ? 1 : 0));
				dest.writeByte((byte) (bothblocks ? 1 : 0));
				dest.writeByte((byte) (sticky ? 1 : 0));
				dest.writeByte((byte) (special ? 1 : 0));
				dest.writeByte((byte) (cancelled ? 1 : 0));
				dest.writeByte((byte) (favorite ? 1 : 0));
		}
	}

	public static final Parcelable.Creator<Detail> CREATOR = new Parcelable.Creator<Detail>() {
		@Override
		public Detail createFromParcel(Parcel in) {
			return new Detail(in);
		}

		@Override
		public Detail[] newArray(int size) {
			return new Detail[size];
		}
	};
}

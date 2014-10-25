package com.el1t.iocane;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by El1t on 10/21/14.
 */
public class EighthActivityItem implements Serializable
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
	private ArrayList<Integer> blockSponsors;
	private ArrayList<Integer> blockRooms;
	private String blockRoomString;
	private int bid;
	private boolean cancelled;
	private String comment;
	private String advertisement;
	private boolean attendanceTaken;
	private boolean favorite;
	private int memberCount;
	private int capacity;

	public EighthActivityItem(int AID, String name, String description, boolean restricted, boolean presign, boolean oneaday,
	                          boolean bothblocks, boolean sticky, boolean special, boolean calendar, boolean roomChanged, ArrayList<Integer> blockSponsors,
	                          ArrayList<Integer> blockRooms, String blockRoomString, int bid, boolean cancelled, String comment, String advertisement,
	                          boolean attendanceTaken, boolean favorite, int memberCount, int capacity) {
		this.AID = AID;
		this.name = name.trim();
		this.description = description.trim();
		this.restricted = restricted;
		this.presign = presign;
		this.oneaday = oneaday;
		this.bothblocks = bothblocks;
		this.sticky = sticky;
		this.special = special;
		this.calendar = calendar;
		this.roomChanged = roomChanged;
		this.blockSponsors = blockSponsors;
		this.blockRooms = blockRooms;
		this.blockRoomString = blockRoomString.trim();
		this.bid = bid;
		this.cancelled = cancelled;
		this.comment = comment.trim();
		this.advertisement = advertisement.trim();
		this.attendanceTaken = attendanceTaken;
		this.favorite = favorite;
		this.memberCount = memberCount;
		this.capacity = capacity;
	}

	public EighthActivityItem(int AID, String name, String description, boolean restricted, boolean presign, boolean oneaday,
	                          boolean bothblocks, boolean sticky, boolean special, boolean calendar, ArrayList<Integer> blockSponsors,
	                          ArrayList<Integer> blockRooms, int bid, boolean cancelled, String comment, String advertisement,
	                          boolean attendanceTaken) {
		this.AID = AID;
		this.name = name.trim();
		this.description = description.trim();
		this.restricted = restricted;
		this.presign = presign;
		this.oneaday = oneaday;
		this.bothblocks = bothblocks;
		this.sticky = sticky;
		this.special = special;
		this.calendar = calendar;
		this.blockSponsors = blockSponsors;
		this.blockRooms = blockRooms;
		this.bid = bid;
		this.cancelled = cancelled;
		this.comment = comment.trim();
		this.advertisement = advertisement.trim();
		this.attendanceTaken = attendanceTaken;
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

	public boolean isRestricted() {
		return restricted;
	}

	public boolean isPresign() {
		return presign;
	}

	public boolean isOneaday() {
		return oneaday;
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

	public boolean isCalendar() {
		return calendar;
	}

	public boolean isRoomChanged() {
		return roomChanged;
	}

	public ArrayList<Integer> getBlockSponsors() {
		return blockSponsors;
	}

	public ArrayList<Integer> getBlockRooms() {
		return blockRooms;
	}

	public String getBlockRoomString() {
		return blockRoomString;
	}

	public int getBid() {
		return bid;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String getComment() {
		return comment;
	}

	public String getAdvertisement() {
		return advertisement;
	}

	public boolean isAttendanceTaken() {
		return attendanceTaken;
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

	public boolean hasDescription() {
		return description.equals("") || description.trim().toLowerCase().equals("no description available");
	}
}

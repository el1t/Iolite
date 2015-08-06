package com.el1t.iolite.item;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by El1t on 11/22/2014.
 */
public class User implements Parcelable {
	private String mUID; // UID is an integer, but only used as a string
	private String mUsername;
	private String[] mName;
	private Address mAddress;
	private String[] mEmails;
	private String mMobile;
	private int mGradYear;

	public User(String UID, String username, String[] name, Address address, String[] emails,
	            String mobile, int gradYear) {
		this.mUID = UID;
		this.mUsername = username;
		this.mName = name;
		this.mAddress = address;
		this.mEmails = emails;
		this.mMobile = mobile;
		this.mGradYear = gradYear;
	}

	public static class UserBuilder {
		private String UID;
		private String username;
		private String[] name;
		private Address address;
		private String[] emails;
		private String mobile;
		private int gradYear;

		public UserBuilder() {
			this.address = new Address(Locale.ENGLISH);
			this.name = new String[]{"", "", ""};
		}

		public UserBuilder UID(String UID) {
			this.UID = UID;
			return this;
		}

		public UserBuilder username(String username) {
			this.username = username;
			return this;
		}

		public UserBuilder firstName(String first) {
			if (first != null) {
				this.name[0] = first;
			}
			return this;
		}

		public UserBuilder middleName(String middle) {
			if (middle != null) {
				this.name[1] = middle;
			}
			return this;
		}

		public UserBuilder lastName(String last) {
			if (last != null) {
				this.name[2] = last;
			}
			return this;
		}

		public UserBuilder street(String street) {
			this.address.setAddressLine(0, street);
			return this;
		}

		public UserBuilder city(String city) {
			this.address.setSubAdminArea(city);
			return this;
		}

		public UserBuilder state(String state) {
			this.address.setAdminArea(state);
			return this;
		}

		public UserBuilder postalCode(String postalCode) {
			this.address.setPostalCode(postalCode);
			return this;
		}

		public UserBuilder emails(String[] emails) {
			this.emails = emails;
			return this;
		}

		public UserBuilder phone(String home) {
			this.address.setPhone(home);
			return this;
		}

		public UserBuilder mobile(String mobile) {
			this.mobile = mobile;
			return this;
		}

		public UserBuilder gradYear(int gradYear) {
			this.gradYear = gradYear;
			return this;
		}

		public User build() {
			return new User(UID, username, name, address, emails, mobile, gradYear);
		}
	}

	public String getUID() {
		return mUID;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getFullName() {
		return mName[0] + " " + mName[1] + " " + mName[2];
	}

	public String getShortName() {
		return mName[0] + " " + mName[2];
	}

	public Address getAddress() {
		return mAddress;
	}

	public String getAddressString() {
		return mAddress.getAddressLine(0) + "\n" +
				mAddress.getSubAdminArea() + ", " + mAddress.getAdminArea() + " " + mAddress.getPostalCode();
	}

	public String getPhone() {
		return mAddress.getPhone();
	}

	public String[] getEmails() {
		return mEmails;
	}

	public String getMobile() {
		return mMobile;
	}

	public int getGradYear() {
		return mGradYear;
	}

	protected User(Parcel in) {
		mUID = in.readString();
		mUsername = in.readString();
		mName = in.createStringArray();
		mAddress = in.readParcelable(Address.class.getClassLoader());
		mEmails = in.createStringArray();
		mMobile = in.readString();
		mGradYear = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUID);
		dest.writeString(mUsername);
		dest.writeStringArray(mName);
		dest.writeParcelable(mAddress, flags);
		dest.writeStringArray(mEmails);
		dest.writeString(mMobile);
		dest.writeInt(mGradYear);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
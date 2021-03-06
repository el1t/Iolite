package com.el1t.iolite.model;

import android.graphics.Bitmap;
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
	private Bitmap mPicture;

	public User(String UID, String username, String[] name, Address address, String[] emails,
	            String mobile, int gradYear, Bitmap picture) {
		this.mUID = UID;
		this.mUsername = username;
		this.mName = name;
		this.mAddress = address;
		this.mEmails = emails;
		this.mMobile = mobile;
		this.mGradYear = gradYear;
		this.mPicture = picture;
	}

	public static class Builder {
		private String UID;
		private String username;
		private String[] name;
		private Address address;
		private String[] emails;
		private String mobile;
		private int gradYear;
		private Bitmap picture;

		public Builder() {
			this.address = new Address(Locale.ENGLISH);
			this.name = new String[]{"", "", ""};
		}

		public Builder(User user) {
			this.UID = user.getUID();
			this.username = user.getUsername();
			this.name = user.getName();
			this.address = user.getAddress();
			this.emails = user.getEmails();
			this.mobile = user.getMobile();
			this.gradYear = user.getGradYear();
			this.picture = user.getPicture();
		}

		public Builder UID(String UID) {
			this.UID = UID;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder firstName(String first) {
			if (first != null) {
				this.name[0] = first;
			}
			return this;
		}

		public Builder middleName(String middle) {
			if (middle != null) {
				this.name[1] = middle;
			}
			return this;
		}

		public Builder lastName(String last) {
			if (last != null) {
				this.name[2] = last;
			}
			return this;
		}

		public Builder street(String street) {
			this.address.setAddressLine(0, street);
			return this;
		}

		public Builder city(String city) {
			this.address.setSubAdminArea(city);
			return this;
		}

		public Builder state(String state) {
			this.address.setAdminArea(state);
			return this;
		}

		public Builder postalCode(String postalCode) {
			this.address.setPostalCode(postalCode);
			return this;
		}

		public Builder emails(String[] emails) {
			this.emails = emails;
			return this;
		}

		public Builder phone(String home) {
			this.address.setPhone(home);
			return this;
		}

		public Builder mobile(String mobile) {
			this.mobile = mobile;
			return this;
		}

		public Builder gradYear(int gradYear) {
			this.gradYear = gradYear;
			return this;
		}

		public Builder picture(Bitmap picture) {
			this.picture = picture;
			return this;
		}

		public User build() {
			return new User(UID, username, name, address, emails, mobile, gradYear, picture);
		}
	}

	public String getUID() {
		return mUID;
	}

	public String getUsername() {
		return mUsername;
	}

	public String[] getName() {
		return mName;
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

	public Bitmap getPicture() {
		return mPicture;
	}

	protected User(Parcel in) {
		mUID = in.readString();
		mUsername = in.readString();
		mName = in.createStringArray();
		mAddress = in.readParcelable(Address.class.getClassLoader());
		mEmails = in.createStringArray();
		mMobile = in.readString();
		mGradYear = in.readInt();
		mPicture = in.readParcelable(Bitmap.class.getClassLoader());
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
		dest.writeValue(mPicture);
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
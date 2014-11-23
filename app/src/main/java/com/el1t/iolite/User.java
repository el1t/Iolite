package com.el1t.iolite;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by El1t on 11/22/2014.
 */
public class User implements Parcelable
{
	private String mUID; // UID is an integer, but only used as a string
	private String mUsername;
	private String[] mName;
	private Address mAddress;
	private ArrayList<String> mEmails;
	private String mMobile;
	private int mGradYear;

	public User() {
		mUID = mMobile = "";
		mName = new String[] {"", "", ""};
		mAddress = new Address(Locale.ENGLISH);
		mEmails = new ArrayList<String>();
	}

	public String getUID() {
		return mUID;
	}

	public void setUID(String UID) {
		mUID = UID;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getFullName() {
		return mName[0] + " " + mName[1] + " " + mName[2];
	}

	public String getShortName() {
		return mName[0] + " " + mName[2];
	}

	public void setFirstName(String name) {
		mName[0] = name;
	}

	public void setMiddleName(String name) {
		mName[1] = name;
	}

	public void setLastName(String name) {
		mName[2] = name;
	}

	public Address getAddress() {
		return mAddress;
	}

	public String getAddressString() {
		return mAddress.getAddressLine(0) + "\n" +
				mAddress.getSubAdminArea() + ", " + mAddress.getAdminArea() + " " + mAddress.getPostalCode();
	}

	public void setStreet(String street) {
		mAddress.setAddressLine(0, street);
	}

	public void setCity(String city) {
		mAddress.setSubAdminArea(city);
	}

	public void setState(String state) {
		mAddress.setAdminArea(state);
	}

	public void setPostalCode(String postalCode) {
		mAddress.setPostalCode(postalCode);
	}

	public String getPhone() {
		return mAddress.getPhone();
	}

	public void setPhone(String phone) {
		mAddress.setPhone(phone);
	}

	public ArrayList<String> getEmails() {
		return mEmails;
	}

	public void addEmail(String email) {
		mEmails.add(email);
	}

	public String getMobile() {
		return mMobile;
	}

	public void setMobile(String mobile) {
		mMobile = mobile;
	}

	public int getGradYear() {
		return mGradYear;
	}

	public void setGradYear(int gradYear) {
		mGradYear = gradYear;
	}

	protected User(Parcel in) {
		mUID = in.readString();
		mUsername = in.readString();
		mName = in.createStringArray();
		mAddress = in.readParcelable(Address.class.getClassLoader());
		if (in.readByte() == 1) {
			mEmails = new ArrayList<String>();
			in.readList(mEmails, String.class.getClassLoader());
		} else {
			mEmails = null;
		}
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
		if (mEmails == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeList(mEmails);
		}
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
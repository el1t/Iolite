package com.el1t.iolite.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by El1t on 8/4/15.
 */
public class NewsPost implements Parcelable {
	private String URL;
	private int ID;
	private String title;
	private String content;
	private String author;
	private int user;
	private Date added;
	private Date updated;
	private String dateString;

	public NewsPost(String URL, int ID, String title, String content, String author, int user,
	                Date added, Date updated, String dateString) {
		this.URL = URL;
		this.ID = ID;
		this.title = title;
		this.content = content;
		this.author = author;
		this.user = user;
		this.added = added;
		this.updated = updated;
		this.dateString = dateString;
	}

	public static class Builder {
		private static final DateFormat FORMAT = new SimpleDateFormat("M/d/yy", Locale.US);
		private String URL;
		private int ID;
		private String title;
		private String content;
		private String author;
		private int user;
		private Date added;
		private Date updated;

		public Builder URL(String URL) {
			this.URL = URL;
			return this;
		}

		public Builder ID(int ID) {
			this.ID = ID;
			return this;
		}

		public Builder title(String title) {
			this.title = fix(title);
			if (this.title.isEmpty()) {
				this.title = "<i>Untitled</i>";
			}
			return this;
		}

		public Builder content(String content) {
			this.content = fix(content);
			if (this.content.isEmpty()) {
				this.content = "<i>No content</i>";
			}
			return this;
		}

		public Builder author(String author) {
			this.author = author;
			return this;
		}

		public Builder user(int user) {
			this.user = user;
			return this;
		}

		public Builder added(Date added) {
			this.added = added;
			return this;
		}

		public Builder updated(Date updated) {
			this.updated = updated;
			return this;
		}

		/**
		 * Fix unusual characters present in news
		 *
		 * @param malformedString Raw string from API request
		 * @return Cleaned string
		 */
		private String fix(String malformedString) {
			return malformedString.replace("â€“", ":")
					.replace("â€™", "&#39;")
					.replaceAll("(â€.|Â)", "")
					.trim();
		}

		public NewsPost build() {
			final String dateString;
			if (added.equals(updated)) {
				dateString = "Posted on " + FORMAT.format(added);
			} else {
				dateString = "Updated on " + FORMAT.format(updated);
			}
			return new NewsPost(URL, ID, title, content, author, user, added, updated, dateString);
		}
	}

	public String getURL() {
		return URL;
	}

	public int getID() {
		return ID;
	}

	public Spanned getTitle() {
		return Html.fromHtml(title);
	}

	public String getContent() {
		return content;
	}

	public Spanned getTrimmedContent() {
		return Html.fromHtml(content.replaceAll("(\\s{2}|<br\\s?/>)", " ").trim());
	}

	public String getAuthor() {
		return author;
	}

	public int getUser() {
		return user;
	}

	public Date getAdded() {
		return added;
	}

	public Date getUpdated() {
		return updated;
	}

	public String getDateString() {
		return dateString;
	}

	protected NewsPost(Parcel in) {
		URL = in.readString();
		ID = in.readInt();
		title = in.readString();
		content = in.readString();
		author = in.readString();
		user = in.readInt();
		added = new Date(in.readLong());
		updated = new Date(in.readLong());
		dateString = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(URL);
		dest.writeInt(ID);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(author);
		dest.writeInt(user);
		dest.writeLong(added.getTime());
		dest.writeLong(updated.getTime());
		dest.writeString(dateString);
	}

	public static final Parcelable.Creator<NewsPost> CREATOR = new Parcelable.Creator<NewsPost>() {
		@Override
		public NewsPost createFromParcel(Parcel in) {
			return new NewsPost(in);
		}

		@Override
		public NewsPost[] newArray(int size) {
			return new NewsPost[size];
		}
	};
}

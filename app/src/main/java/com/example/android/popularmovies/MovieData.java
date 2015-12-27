package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nikhil.p on 27/12/15.
 */
public class MovieData implements Parcelable {

    public long movie_id;
    public String title;
    public String overview;
    public String poster_path;
    public String backdrop_path;
    public String release_date;
    public float vote_average;
    public float popularity;
    public int vote_count;

    public MovieData() {
    }

    public MovieData(Parcel in) {
        movie_id = in.readLong();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        release_date = in.readString();
        vote_average = in.readFloat();
        popularity = in.readFloat();
        vote_count = in.readInt();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return (new MovieData(parcel));
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(movie_id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(poster_path);
        parcel.writeString(backdrop_path);
        parcel.writeString(release_date);
        parcel.writeFloat(vote_average);
        parcel.writeFloat(popularity);
        parcel.writeInt(vote_count);
    }
}

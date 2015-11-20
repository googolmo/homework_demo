package im.amomo.homework.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class Item implements Parcelable {
    public static final String TABLE = "item";

    public interface Columns {
        String ID = "_id";
        String TITLE = TABLE + "_title";
        String URL = TABLE + "_url";
        String IMAGE_URL = TABLE + "_image_url";
        String IMAGE_WIDTH = TABLE + "_image_width";
        String IMAGE_HEIGHT = TABLE + "_image_height";
        String POINTS = TABLE + "_points";
        String COMMENTS = TABLE + "_comments";
    }

    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS `" + TABLE + "` ( `" +
            Columns.ID + "` INTEGER PRIMARY KEY, `" +
            Columns.TITLE + "` TEXT, `" +
            Columns.URL + "` TEXT, `" +
            Columns.IMAGE_URL + "` TEXT, `" +
            Columns.IMAGE_WIDTH + "` INTEGER, `" +
            Columns.IMAGE_HEIGHT + "` INTEGER, `" +
            Columns.POINTS + "` INTEGER, `" +
            Columns.COMMENTS + "` INTEGER," +
            "UNIQUE (" + Columns.ID + ") ON CONFLICT REPLACE)";

    public long id;
    public String title;
    public String url;
    public Image image;
    public int points;
    public int comments;


    public Item() {
    }

    protected Item(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.url = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.points = in.readInt();
        this.comments = in.readInt();
    }

    public Item(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(Columns.ID));
        this.title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
        this.url = cursor.getString(cursor.getColumnIndex(Columns.URL));
        this.image = new Image(cursor);
        this.points = cursor.getInt(cursor.getColumnIndex(Columns.POINTS));
        this.comments = cursor.getInt(cursor.getColumnIndex(Columns.COMMENTS));
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Columns.ID, this.id);
        cv.put(Columns.TITLE, this.title);
        cv.put(Columns.URL, this.url);
        cv.put(Columns.IMAGE_URL, this.image.url);
        cv.put(Columns.IMAGE_HEIGHT, this.image.height);
        cv.put(Columns.IMAGE_WIDTH, this.image.width);
        cv.put(Columns.POINTS, this.points);
        cv.put(Columns.COMMENTS, this.comments);
        return cv;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeParcelable(this.image, flags);
        dest.writeInt(this.points);
        dest.writeInt(this.comments);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", points=" + points +
                ", comments=" + comments +
                '}';
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel source) {return new Item(source);}

        public Item[] newArray(int size) {return new Item[size];}
    };

    public static class Image implements Parcelable {
        public String url;
        public int width;
        public int height;

        public Image() {
        }

        public Image(Cursor cursor) {
            this.url = cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL));
            this.height = cursor.getInt(cursor.getColumnIndex(Columns.IMAGE_HEIGHT));
            this.width = cursor.getInt(cursor.getColumnIndex(Columns.IMAGE_WIDTH));
        }

        protected Image(Parcel in) {
            this.url = in.readString();
            this.width = in.readInt();
            this.height = in.readInt();
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
            dest.writeInt(this.width);
            dest.writeInt(this.height);
        }

        @Override
        public String toString() {
            return "Image{" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }

        public static final Creator<Image> CREATOR = new Creator<Image>() {
            public Image createFromParcel(Parcel source) {return new Image(source);}

            public Image[] newArray(int size) {return new Image[size];}
        };
    }
}

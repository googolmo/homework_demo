package im.amomo.homework.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class Item implements Parcelable {
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

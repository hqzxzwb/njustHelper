package com.njust.helper.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Keep;

@Keep
public class Course implements Parcelable {
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    private String id, name, teacher;
    private String classroom, week1, week2;
    private int sec1, sec2, day;

    public Course() {
    }

    protected Course(Parcel in) {
        id = in.readString();
        name = in.readString();
        teacher = in.readString();
        classroom = in.readString();
        week1 = in.readString();
        week2 = in.readString();
        sec1 = in.readInt();
        sec2 = in.readInt();
        day = in.readInt();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getClassroom() {
        return TextUtils.isEmpty(classroom) ? "[教室未知]" : classroom;
    }

    public String getWeek1() {
        return week1;
    }

    public String getWeek2() {
        return week2;
    }

    public int getSec1() {
        return sec1;
    }

    public int getSec2() {
        return sec2;
    }

    public int getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", classroom='" + classroom + '\'' +
                ", week1='" + week1 + '\'' +
                ", week2='" + week2 + '\'' +
                ", sec1=" + sec1 +
                ", sec2=" + sec2 +
                ", day=" + day +
                '}';
    }

    @Override
    public int describeContents() {
        return getClass().getName().hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(teacher);
        dest.writeString(classroom);
        dest.writeString(week1);
        dest.writeString(week2);
        dest.writeInt(sec1);
        dest.writeInt(sec2);
        dest.writeInt(day);
    }
}

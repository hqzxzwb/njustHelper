package com.njust.helper.model;

import java.util.List;

public class CourseData {
    private String courseall;
    private String startdate;
    private List<CourseInfo> infos;
    private List<CourseLoc> locs;

    public String getCourseall() {
        return courseall;
    }

    public void setCourseall(String courseall) {
        this.courseall = courseall;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public List<CourseInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<CourseInfo> infos) {
        this.infos = infos;
    }

    public List<CourseLoc> getLocs() {
        return locs;
    }

    public void setLocs(List<CourseLoc> locs) {
        this.locs = locs;
    }
}
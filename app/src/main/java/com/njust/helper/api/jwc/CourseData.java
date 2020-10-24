package com.njust.helper.api.jwc;

import java.util.List;

public class CourseData {
    private List<CourseInfo> infos;
    private List<CourseLoc> locs;

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

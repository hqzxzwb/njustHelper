package com.njust.helper.tools;

import com.njust.helper.BuildConfig;

public final class Constants {
    public static final String DEFAULT_SEMESTER_START = "2019-02-25";

    public static final long[] NOTIFICATION_VIBRATION_TIME = {0, 200, 100, 200};
    public static final int NOTIFICATION_CODE_COURSE = 0;
    public static final int NOTIFICATION_CODE_UPDATE = 1;

    public static final int MAX_WEEK_COUNT = 25;
    public static final int[] SECTION_START = {28800000, 38400000, 50400000, 57000000, 68400000};
    public static final int[] SECTION_END = {37500000, 44100000, 56100000, 65700000, 77100000};

    public static final String FILE_PROVIDER_AUTH = BuildConfig.APPLICATION_ID + ".files";

    public static final String EXTRA_ID = "id";

    public static final int COURSE_SECTION_COUNT = 5;
}

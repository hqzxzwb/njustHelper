package com.zwb.commonlibs.http;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class HttpResponse {
    public int responseCode;
    public InputStream inputStream;
    public IOException suppressedIOException;

    public String getStringResult() {
        if (inputStream == null) {
            return null;
        }
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFailureText() {
        if (suppressedIOException != null) {
            return suppressedIOException.getClass().getSimpleName();
        } else {
            return String.valueOf(responseCode);
        }
    }
}

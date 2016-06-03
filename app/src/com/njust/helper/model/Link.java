package com.njust.helper.model;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class Link {
    private String name, url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void openUrl(View view) {
        Intent intent = new Intent().setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        view.getContext().startActivity(intent);
    }
}

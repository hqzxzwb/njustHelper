package com.njust.helper.library.book;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.njust.helper.R;

import java.util.ArrayList;
import java.util.List;

public class LibDetailAdapter extends Adapter<LibDetailAdapter.LibDetailHolder> {
    private final LayoutInflater mInflater;
    private List<LibDetailItem> mDetailItems = new ArrayList<>();
    private String head;

    public LibDetailAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(LibDetailActivity.LibDetailData data) {
        mDetailItems = data.getStates();
        head = data.getHead();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDetailItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(LibDetailHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                holder.code.setText(head);
                break;
            default:
                holder.setItem(mDetailItems.get(position - 1));
        }
    }

    @Override
    public LibDetailHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        int type = getItemViewType(arg1);
        View view = mInflater.inflate(type == 1 ? R.layout.item_lib_detail
                : R.layout.item_text, arg0, false);
        return new LibDetailHolder(view, type);
    }

    public static class LibDetailHolder extends ViewHolder {
        TextView code;
        TextView place;
        TextView state;

        LibDetailHolder(View itemView, int type) {
            super(itemView);
            switch (type) {
                case 0:
                    code = (TextView) itemView;
                    break;
                default:
                    code = itemView.findViewById(R.id.textView1);
                    place = itemView.findViewById(R.id.textView2);
                    state = itemView.findViewById(R.id.textView3);
            }
        }

        public void setItem(LibDetailItem item) {
            code.setText(Html.fromHtml(item.getCode()));
            place.setText(item.getPlace());
            state.setText(Html.fromHtml(item.getState()));
        }
    }
}

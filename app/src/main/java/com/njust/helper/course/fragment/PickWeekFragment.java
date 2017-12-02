package com.njust.helper.course.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.njust.helper.R;
import com.njust.helper.databinding.ItemPickWeekBinding;
import com.njust.helper.tools.Constants;
import com.njust.helper.tools.DataBindingHolder;

/**
 * Created by zwb on 2016/4/4.
 * 选择周次
 */
public class PickWeekFragment extends BottomSheetDialogFragment {
    private PickWeekAdapter adapter = new PickWeekAdapter(this);
    Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_course_week, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (Listener) context;
    }

    public void setChosenWeek(int week) {
        adapter.setChosenWeek(week);
    }

    public interface Listener {
        void setWeek(int week);
    }

    private static class PickWeekAdapter extends RecyclerView.Adapter<DataBindingHolder<ItemPickWeekBinding>> {
        private int chosenWeek;
        private PickWeekFragment fragment;

        PickWeekAdapter(PickWeekFragment fragment) {
            this.fragment = fragment;
        }


        @Override
        public DataBindingHolder<ItemPickWeekBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemPickWeekBinding binding = ItemPickWeekBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false);
            return new DataBindingHolder<>(binding);
        }

        @Override
        public void onBindViewHolder(DataBindingHolder<ItemPickWeekBinding> holder, int position) {
            holder.getDataBinding().setWeek(position);
            holder.getDataBinding().setChosen(chosenWeek == position + 1);
            final int p = holder.getAdapterPosition();
            holder.itemView.setOnClickListener(v -> {
                fragment.listener.setWeek(p + 1);
                fragment.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return Constants.MAX_WEEK_COUNT;
        }

        public void setChosenWeek(int chosenWeek) {
            this.chosenWeek = chosenWeek;
            notifyDataSetChanged();
        }
    }
}

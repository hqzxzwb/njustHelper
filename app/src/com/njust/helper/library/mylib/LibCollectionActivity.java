package com.njust.helper.library.mylib;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.njust.helper.R;
import com.njust.helper.activity.BaseActivity;
import com.njust.helper.databinding.ItemLibCollectBinding;
import com.njust.helper.library.LibDetailActivity;
import com.njust.helper.model.LibCollectItem;
import com.njust.helper.tools.DataBindingHolder;
import com.njust.helper.tools.Prefs;
import com.zwb.commonlibs.injection.ViewInjection;
import com.zwb.commonlibs.ui.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LibCollectionActivity extends BaseActivity {
    private static final int REQUEST_CODE_LIB_DETAIL = 0;
    private LibraryDatabaseManager manager;
    private LibCollectionAdapter adapter;
    private List<LibCollectItem> mList;
    private List<String> itemsToRemove = new ArrayList<>();

    @ViewInjection(R.id.recyclerView)
    private RecyclerView recyclerView;
    @ViewInjection(R.id.coordinatorLayout)
    private CoordinatorLayout coordinatorLayout;
    @ViewInjection(R.id.textView1)
    private TextView emptyView;

    @Override
    protected int layoutRes() {
        return R.layout.activity_lib_collection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = LibraryDatabaseManager.getInstance(this);
        mList = manager.findCollect();
        adapter = new LibCollectionAdapter(mList, this);
        adapter.setListener(new LibCollectionAdapter.OnEmptyStateChangeListener() {
            @Override
            public void onEmptyStateChange(boolean empty) {
                emptyView.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.setOnDeleteInDialogListener(new LibCollectionAdapter.OnDeleteInDialogListener() {
            @Override
            public void onDeleteInDialog(RecyclerView.ViewHolder viewHolder) {
                deleteItem(viewHolder.getAdapterPosition());
            }
        });

        if (!Prefs.getLibCollectionHint(this)) {
            showSnack("图书详情页可以收藏\n左右滑动条目以删除", "不再提示", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.putLibCollectionHint(LibCollectionActivity.this, true);
                }
            });
        }
    }

    private void deleteItem(int position) {
        itemsToRemove.add(adapter.delete(position).getId());
        showSnack("您删除了一本图书", "撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibCollectItem libCollectItem = adapter.restore();
                if (libCollectItem != null) {
                    itemsToRemove.remove(libCollectItem.getId());
                }
                showSnack("已撤销更改");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View getViewForSnackBar() {
        return coordinatorLayout;
    }

    private void showLibDetail(String id) {
        Intent intent = new Intent(this, LibDetailActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, REQUEST_CODE_LIB_DETAIL);
    }

    @Override
    public void onBackPressed() {
        if (itemsToRemove.size() == 0) {
            finish();
        } else {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        manager.removeCollects(itemsToRemove);
                    }
                    finish();
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle("注意")
                    .setMessage("您对收藏的图书作出了更改，是否确认保存？")
                    .setPositiveButton("保存更改", listener)
                    .setNegativeButton("放弃更改", listener)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LIB_DETAIL) {
            if (resultCode == RESULT_OK) {
                if (!data.getBooleanExtra("isCollected", true)) {
                    String id = data.getStringExtra("id");
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).getId().equals(id)) {
                            adapter.delete(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static class LibCollectionAdapter extends RecyclerView.Adapter<DataBindingHolder<ItemLibCollectBinding>> {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        private List<LibCollectItem> mData;
        private LibCollectionActivity mActivity;
        private LibCollectItem restoreItem;
        private int restorePosition;
        private OnDeleteInDialogListener onDeleteInDialogListener;
        private OnEmptyStateChangeListener listener;

        public LibCollectionAdapter(List<LibCollectItem> data, LibCollectionActivity activity) {
            this.mData = data;
            this.mActivity = activity;
        }

        public static String getDateString(long time) {
            return "收藏时间：" + DATE_FORMAT.format(new Date(time));
        }

        public static void handleClick(View view, String id) {
            LibDetailActivity.showLibDetail(view.getContext(), id);
        }

        public void setOnDeleteInDialogListener(OnDeleteInDialogListener onDeleteInDialogListener) {
            this.onDeleteInDialogListener = onDeleteInDialogListener;
        }

        @Override
        public DataBindingHolder<ItemLibCollectBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DataBindingHolder<>(ItemLibCollectBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(final DataBindingHolder<ItemLibCollectBinding> holder, int position) {
            final LibCollectItem libCollectItem = mData.get(position);
            holder.getDataBinding().setItem(libCollectItem);
            final String id = mData.get(position).getId();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.showLibDetail(id);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("确定删除这条收藏吗?")
                            .setMessage(libCollectItem.getName())
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (onDeleteInDialogListener != null) {
                                        onDeleteInDialogListener.onDeleteInDialog(holder);
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    return true;
                }
            });
        }

        public LibCollectItem delete(int position) {
            restoreItem = mData.remove(position);
            restorePosition = position;
            notifyItemRemoved(position);
            listener.onEmptyStateChange(mData.isEmpty());
            return restoreItem;
        }

        public LibCollectItem restore() {
            if (restoreItem != null) {
                mData.add(restorePosition, restoreItem);
                listener.onEmptyStateChange(mData.isEmpty());
                notifyItemInserted(restorePosition);
                LibCollectItem libCollectItem = restoreItem;
                restoreItem = null;
                return libCollectItem;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void setListener(OnEmptyStateChangeListener listener) {
            this.listener = listener;
            listener.onEmptyStateChange(mData.isEmpty());
        }

        interface OnDeleteInDialogListener {
            void onDeleteInDialog(RecyclerView.ViewHolder viewHolder);
        }

        interface OnEmptyStateChangeListener {
            void onEmptyStateChange(boolean empty);
        }
    }
}

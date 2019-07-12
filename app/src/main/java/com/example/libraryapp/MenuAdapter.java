package com.example.libraryapp;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private String[] menuList = {"Books", "Recommend", "Search", "My Account"};

    public MenuAdapter(Context mContext) {
        context = mContext;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public View totalView;
        public TextView nameView;
        public MenuViewHolder(View v, Context mContext) {
            super(v);
            context = mContext;
            totalView = v;
            nameView = v.findViewById(R.id.menu_text);
        }

        public TextView getNameView() {
            return nameView;
        }
    }

    @Override
    public int getItemCount() { return 4; }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);
        MenuViewHolder vh = new MenuViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuViewHolder holder, final int position) {
        holder.nameView.setText(menuList[position]);
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.nameView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.right_to_left));
            }
        }, 50 * position);
        if (position == 0) {
            holder.nameView.setPaintFlags(holder.nameView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}

package com.opetbot.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.opetbot.GetterSetter.gettersetterpowerfunction;
import com.opetbot.R;

import java.util.List;

/**
 * Created by Softeligent on 12-02-18.
 */

public class BottomSheetPowerFunctionAdapter extends RecyclerView.Adapter<BottomSheetPowerFunctionAdapter.ViewHolder> {

    private List<gettersetterpowerfunction> mItems;
    private PowerFunctionItemListener mListener;

    public BottomSheetPowerFunctionAdapter(List<gettersetterpowerfunction> items, PowerFunctionItemListener listener) {
        mItems = items;
        mListener = listener;
    }

    public void setListener(PowerFunctionItemListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottomsheetpowerfunction, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView;
        public gettersetterpowerfunction item;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void setData(gettersetterpowerfunction item) {
            this.item = item;
            imageView.setImageResource(item.getDrawableResource());
            textView.setText(item.getTitle());
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.PowerFunctionItemListener(item);
            }
        }
    }

    public interface PowerFunctionItemListener {
        void PowerFunctionItemListener(gettersetterpowerfunction item);
    }
}

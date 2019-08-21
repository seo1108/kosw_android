package kr.co.photointerior.kosw.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import kr.co.photointerior.kosw.listener.ItemClickListener;

public class SpinnerRecyclerAdapter extends RecyclerView.Adapter<SpinnerRecyclerAdapter.BaseRowHolder>{
    private ItemClickListener mClickListener;

    @NonNull
    @Override
    public BaseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRowHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public abstract class BaseRowHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public BaseRowHolder(View itemView) {
            super(itemView);
            pickupView();
        }

        protected abstract void pickupView();


    }
}

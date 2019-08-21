package kr.co.photointerior.kosw.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseRowHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
    public BaseRowHolder(View itemView) {
        super(itemView);
        pickupView();
    }

    protected abstract void pickupView();

}

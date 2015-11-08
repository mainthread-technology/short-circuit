package technology.mainthread.shortcircuit.nearby;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import technology.mainthread.shortcircuit.R;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    private final List<NearbyItem> nearbyItem;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_label)
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public NearbyAdapter() {
        this.nearbyItem = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NearbyItem nearbyItem = this.nearbyItem.get(position);

        holder.mTextView.setText(nearbyItem.getId() + ", " + nearbyItem.getDevice() + "," + nearbyItem.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return nearbyItem.size();
    }

    public void addItem(NearbyItem item) {
        nearbyItem.add(item);
    }
}

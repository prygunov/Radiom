package net.artux.radio.ui.home;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.artux.radio.R;
import net.artux.radio.model.Station;

import java.util.ArrayList;
import java.util.List;

public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewAdapter.StationViewHolder> {


    static class StationViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView stationTitle;
        ImageView stationLogo;

        StationViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            stationTitle = itemView.findViewById(R.id.stationTitle);
            stationLogo = itemView.findViewById(R.id.stationLogo);
        }

        void bind(Station st, int pos, OnItemClick itemClickListener){
            Picasso.get()
                    .load(st.imageUrl)
                    .into(stationLogo, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap b = ((BitmapDrawable) stationLogo.getDrawable()).getBitmap();
                            Palette p = Palette.from(b).generate();
                            int color = p.getDarkVibrantColor(stationLogo.getContext().getResources().getColor(R.color.defTextColor));
                            stationTitle.setTextColor(color);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
            stationTitle.setText(st.title);
            stationTitle.setTransitionName(""+pos);
            itemView.setOnClickListener(view -> itemClickListener.onClick(st, stationTitle, ""+pos));
        }
    }

    interface OnItemClick{
        void onClick(Station station, View view, String sharedName);
    }

    private List<Station> stations = new ArrayList<>();
    private OnItemClick clickListener;


    HomeViewAdapter(OnItemClick clickListener){
        this.clickListener = clickListener;
    }

    HomeViewAdapter(OnItemClick clickListener, List<Station> stations){
        this.clickListener = clickListener;
        this.stations = stations;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        holder.bind(stations.get(position), position, clickListener);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    void addData(List<Station> stations, boolean clear){
        if (clear){
            stations.clear();
        }

        this.stations.addAll(stations);
        notifyDataSetChanged();
    }
}

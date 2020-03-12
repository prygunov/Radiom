package net.artux.radio.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
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
import net.artux.radio.common.BasePresenter;
import net.artux.radio.model.Station;
import net.artux.radio.widget.SquareLayout;

import java.util.List;

public class HomeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class StationViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView stationTitle;
        ImageView stationLogo;

        StationViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            stationTitle = itemView.findViewById(R.id.stationTitle);
            stationLogo = itemView.findViewById(R.id.stationLogo);
        }
    }

    List<Station> stations;
    BasePresenter basePresenter;
    Context context;


    HomeViewAdapter(Context context, List<Station> stations, BasePresenter basePresenter){
        this.context = context;
        this.stations = stations;
        this.basePresenter = basePresenter;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StationViewHolder st = (StationViewHolder) holder;
        Picasso.get()
                .load(stations.get(position).imageUrl)
                .into(st.stationLogo, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap b = ((BitmapDrawable) st.stationLogo.getDrawable()).getBitmap();
                        Palette p = Palette.from(b).generate();
                        int color = p.getDarkVibrantColor(context.getResources().getColor(R.color.defTextColor));
                        st.stationTitle.setTextColor(color);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        st.stationTitle.setText(stations.get(position).title);
        st.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("tag", "onClick");



                basePresenter.changeStation(stations.get(position), 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}

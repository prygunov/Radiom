package net.artux.radio.ui.station;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.radio.R;
import net.artux.radio.model.Stream;

import java.util.List;

public class StreamsAdapter extends RecyclerView.Adapter<StreamsAdapter.StreamViewHolder> {

    class StreamViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView streamImage;
        TextView streamTitle;
        ImageView favorite;


        StreamViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            streamImage = itemView.findViewById(R.id.streamImage);
            streamTitle = itemView.findViewById(R.id.streamTitle);
        }

        void bind(int pos){
            view.setOnClickListener(view1 -> onStreamClick.setStream(pos));
            streamTitle.setText(streams.get(pos).title);
        }
    }

    interface OnStreamClick{
        void setStream(int pos);
        void addToFavorite(Stream stream);
    }

    private List<Stream> streams;
    private OnStreamClick onStreamClick;

    StreamsAdapter(OnStreamClick onStreamClick, List<Stream> streams){
        this.onStreamClick = onStreamClick;
        this.streams = streams;
    }

    @NonNull
    @Override
    public StreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StreamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StreamViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return streams.size();
    }

    public void addData(List<Stream> streams, boolean clear){
        if (clear){
            streams.clear();
        }

        this.streams.addAll(streams);
        notifyDataSetChanged();
    }

}

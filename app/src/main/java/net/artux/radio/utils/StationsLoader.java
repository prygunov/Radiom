package net.artux.radio.utils;

import net.artux.radio.common.MainContract;
import net.artux.radio.model.Qualities;
import net.artux.radio.model.Station;
import net.artux.radio.model.Stream;

import java.util.ArrayList;
import java.util.List;

public class StationsLoader implements MainContract.Repository {

    public static List<Station> getStations(){
        ArrayList<Station> stations = new ArrayList<>();
        Station station = new Station();

        station.title = "Radio Record";
        station.imageUrl = "https://pbs.twimg.com/profile_images/1164866900742082563/-zuRQoYW_400x400.jpg";

        Stream stream = new Stream();
        stream.title = "Chill House";
        stream.types.put(Qualities.b320, "http://air.radiorecord.ru:8102/chillhouse_320");
        stream.types.put(Qualities.b128, "http://air.radiorecord.ru:8102/chillhouse_128");
        station.streams.add(stream);

        stream = new Stream();
        stream.title = "Neuro Funk";
        stream.types.put(Qualities.b320, "http://air.radiorecord.ru:8102/neurofunk_320");
        station.streams.add(stream);
        stations.add(station);
        station = new Station();

        stream = new Stream();
        stream.title = "Europa Plus Main";
        stream.types.put(Qualities.b256, " http://ep256.streamr.ru");
        station.title = "Europa Plus";
        station.imageUrl = "https://europaplus.ru/media/logotype.e7ee9233.png";
        station.streams.add(stream);
        stations.add(station);


        return stations;
    }

}

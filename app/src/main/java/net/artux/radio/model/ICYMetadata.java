package net.artux.radio.model;

public class ICYMetadata extends Stream.MediaData {

    private String title;
    private String url;
    private String rawMetadata;

    public static ICYMetadata fromString(String icy){
        ICYMetadata icyMetadata = new ICYMetadata();

        String[] array = icy.trim().split(", ");

        for(String entry : array){
            String[] field = entry.split("=");
            switch (field[0]) {

                case "title":
                    icyMetadata.title = field[1];
                    break;
                case "url":
                    icyMetadata.url = field[1];
                    break;
                case "rawMetadata":
                    icyMetadata.rawMetadata = field[1];
                    break;
            }
        }

        return icyMetadata;
    }

    public boolean isEmpty(){
        if (title == null)
            return true;
        if (title.trim().equals(""))
            return true;
        return title.substring(1, title.length() - 1).split(" - ").length == 0 || title.equals("");
    }

    public String getArtist(){
        if (title.substring(1, title.length()-1).split(" - ").length !=0)
            return title.substring(1, title.length()-1).split(" - ")[0];
        else
            return null;
    }

    public String getTitle(){
        if (title.substring(1, title.length()-1).split(" - ").length > 1)
            return title.substring(1, title.length()-1).split(" - ")[1];
        else
            return null;
    }

}

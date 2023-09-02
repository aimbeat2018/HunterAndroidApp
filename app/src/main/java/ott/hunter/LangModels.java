package ott.hunter;

public class LangModels {


    public String id;
    public String image;
    public String langUrl;


    public LangModels(String videoId, String videoName, String videoUrl, String videoDuration) {
        this.id = videoId;
        this.image = videoName;
        this.langUrl = videoUrl;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLangUrl() {
        return langUrl;
    }

    public void setLangUrl(String langUrl) {
        this.langUrl = langUrl;
    }
}
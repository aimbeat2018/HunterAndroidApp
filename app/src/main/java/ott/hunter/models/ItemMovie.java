package ott.hunter.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemMovie implements Serializable {
    private String movieId;
    private String movieName;
    private String movieImage;
    private String movieDuration;
    private String movieSubtitle;
    private long movieDuration2;
    private long movieDuration3;
    private String rental_plan;
    private boolean isPurchased;

    public long getMovieDuration3() {
        return movieDuration3;
    }

    public void setMovieDuration3(long movieDuration3) {
        this.movieDuration3 = movieDuration3;
    }

    private String movieDescription;
    private String movieDate;
    private String movieLanguage;
    private String movieType;
    private String movieUrl;
    private boolean isPremium = false;
    private boolean isDownload = false;
    private String downloadUrl;
    private String movieRating;
    private String movieAccess;
    private ArrayList<Videos> listVideo;
    private ArrayList<Resolution> lstResolution;
    private String strTime;

    public long getMovieDuration2() {
        return movieDuration2;
    }

    public void setMovieDuration2(long movieDuration2) {
        this.movieDuration2 = movieDuration2;
    }

    public ItemMovie() {

    }

    public ItemMovie(long videoDuration) {
        this.movieDuration2 = videoDuration;
    }

    public ItemMovie(String movieId, long videoDuration1) {
        this.movieId=movieId;
        this.movieDuration3 = videoDuration1;
    }


    public ItemMovie(String videoId, String videoName, String videoUrl, String videoDuration) {
        this.movieId = videoId;
        this.movieName = videoName;
        this.movieUrl = videoUrl;
        this.movieDuration = videoDuration;
    }

    public ItemMovie(String videoId, String videoName, String videoDuration, String videoImage, String videoURL) {
        this.movieId = videoId;
        this.movieName = videoName;
        this.movieImage = videoImage;
        this.movieDuration = videoDuration;
        this.movieUrl = videoURL;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieSubtitle() {
        return movieSubtitle;
    }

    public void setMovieSubtitle(String movieSubtitle) {
        this.movieSubtitle = movieSubtitle;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public String getRental_plan() {
        return rental_plan;
    }

    public void setRental_plan(String rental_plan) {
        this.rental_plan = rental_plan;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMovieDuration() {
        return movieDuration;
    }

    public Long getMovieDuration1() {
        return movieDuration2;
    }

    public void setMovieDuration(String movieDuration) {
        this.movieDuration = movieDuration;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }


    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getMovieLanguage() {
        return movieLanguage;
    }

    public void setMovieLanguage(String movieLanguage) {
        this.movieLanguage = movieLanguage;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieAccess() {
        return movieAccess;
    }

    public void setMovieAccess(String movieAccess) {
        this.movieAccess = movieAccess;
    }


    public static class Videos {
        String language;
        String url;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Resolution {
        String movie_id;
        String movie_resolution;
        String movie_url;

        public String getMovie_id() {
            return movie_id;
        }

        public void setMovie_id(String movie_id) {
            this.movie_id = movie_id;
        }

        public String getMovie_resolution() {
            return movie_resolution;
        }

        public void setMovie_resolution(String movie_resolution) {
            this.movie_resolution = movie_resolution;
        }

        public String getMovie_url() {
            return movie_url;
        }

        public void setMovie_url(String movie_url) {
            this.movie_url = movie_url;
        }
    }

    public ArrayList<Resolution> getLstResolution() {
        return lstResolution;
    }

    public void setLstResolution(ArrayList<Resolution> lstResolution) {
        this.lstResolution = lstResolution;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public ArrayList<Videos> getListVideo() {
        return listVideo;
    }

    public void setListVideo(ArrayList<Videos> listVideo) {
        this.listVideo = listVideo;
    }
}

package vn.dise.nytarticlesearch.models;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Article implements Parcelable {

    private String webUrl;
    private String headLine;
    private String thumbNail;

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public String getHeadLine() {
        return headLine;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.setWebUrl(jsonObject.getString("web_url"));
            this.setHeadLine(jsonObject.getJSONObject("headline").getString("main"));
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject media = multimedia.getJSONObject(0);
                this.setThumbNail("http://www.nytimes.com/" + media.getString("url"));
            } else {
                this.setThumbNail("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {

        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getWebUrl());
        dest.writeString(this.getHeadLine());
        dest.writeString(this.getThumbNail());
    }

    protected Article(Parcel in) {
        this.setWebUrl(in.readString());
        this.setHeadLine(in.readString());
        this.setThumbNail(in.readString());
    }
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    public String getWebUrl() {
        return webUrl;
    }
}

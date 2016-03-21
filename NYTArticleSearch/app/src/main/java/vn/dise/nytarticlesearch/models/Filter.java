package vn.dise.nytarticlesearch.models;

import java.util.ArrayList;

/**
 * Created by carot on 3/20/2016.
 */
public class Filter {

    public Filter() {
    }

    public String dateBegin;
    public String sort_type;
    public ArrayList<NEWS_DESK> news_desks_list;

    public enum NEWS_DESK {
        ARTS, FASHION, SPORT
    }

    public Filter(String fromDate, String sType, ArrayList<NEWS_DESK> a) {
        dateBegin = fromDate;
        sort_type = sType;
        news_desks_list = a;
    }


}

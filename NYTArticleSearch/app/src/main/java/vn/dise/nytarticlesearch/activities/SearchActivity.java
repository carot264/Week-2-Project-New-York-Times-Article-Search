package vn.dise.nytarticlesearch.activities;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import vn.dise.nytarticlesearch.adapters.ArticleAdapter;
import vn.dise.nytarticlesearch.models.Article;
import vn.dise.nytarticlesearch.R;
import vn.dise.nytarticlesearch.utils.EndlessRecyclerViewScrollListener;
import vn.dise.nytarticlesearch.utils.SpacesItemDecoration;
import vn.dise.nytarticlesearch.models.Filter;

public class SearchActivity extends AppCompatActivity implements SettingActivity.SettingDialogListener {

    private final String ARTICLE_KEY = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private final String API_KEY = "05b9369fb152acecfc8a5a833a42d298:2:74742145";
    private RecyclerView rvArticle;
    private ArrayList<Article> articles;
    private ArticleAdapter articleAdapter;
    private Filter filterSearch;
    private String querySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Expand the search view and request focus
        // searchView.onActionViewExpanded();
        //searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchArticles(0);
                searchView.clearFocus();
                //reset articleAdapter
                if (articleAdapter.getItemCount() > 0) {
                    articleAdapter.clear();
                }
                querySearch = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishSettingDialog(Filter filter) {
        //Toast.makeText(SearchActivity.this, date, Toast.LENGTH_SHORT).show();
        filterSearch = filter;
    }

    /*
    * FUNCTIONS
    * */
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingActivity settingActivity = SettingActivity.newInstance("Setting");
        settingActivity.show(fm, "Setting");
    }

    public void setupView() {

        //rvArticle.setLayoutManager(new LinearLayoutManager(this));
        // Lookup the recycler-view in activity layout
        rvArticle = (RecyclerView) findViewById(R.id.rvArticle);
        // Initialize Article
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articles);
        // Attach the adapter to the recyclerview to populate items
        rvArticle.setAdapter(articleAdapter);
        // Set layout manager to position the items
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        //enable optimizations if all item views are of the same height and width for significantly smoother scrolling
        rvArticle.setHasFixedSize(true);
        //reflow item
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // Attach the layout manager to the recycler view
        rvArticle.setLayoutManager(gridLayoutManager);

        //set space for each photo in recycleview
        SpacesItemDecoration itemDecoration = new
                SpacesItemDecoration(12);
        rvArticle.addItemDecoration(itemDecoration);

        // Add the scroll listener
        rvArticle.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetchArticles(page);
            }

        });
        filterSearch = new Filter("", "newest", null);
    }
    public String getFilterQuery() {
        String fq = "";

        if (filterSearch.news_desks_list != null) {
            fq = "news_desk:" + "(";
            for (Filter.NEWS_DESK t : filterSearch.news_desks_list) {
                switch (t) {
                    case ARTS:
                        fq = fq + "\"Arts\"" + " ";
                        break;
                    case FASHION:
                        fq = fq + "\"" + "Fashion & Style" + "\"" + " ";
                        break;
                    case SPORT:
                        fq = fq + "\"Sports\"" + " ";
                        break;
                }

            }
            fq = fq + ")";
        }
        if (filterSearch.dateBegin != "") {
            if (fq != "") {
                fq = fq + "&";
            }
            fq = fq + "begin_date=" + filterSearch.dateBegin.replaceAll("/", "");

        }
        return fq;
    }

    public void fetchArticles(int page) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);
        params.put("q", querySearch);

        if (filterSearch.sort_type != "") {
            params.put("sort", "newest");
        }
        if (filterSearch.news_desks_list != null && filterSearch.news_desks_list.size() > 0) {
            params.put("fq", getFilterQuery());
        }

        client.get(ARTICLE_KEY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray result = null;
                try {
                    result = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(result));
                    //adater.notifyDataSetChanged();
                    int curSize = articleAdapter.getItemCount();
                    articleAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
    /*
    * FUNCTIONS
    * */
}

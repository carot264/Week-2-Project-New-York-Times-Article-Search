package vn.dise.nytarticlesearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.dise.nytarticlesearch.R;
import vn.dise.nytarticlesearch.activities.ArticleActivity;
import vn.dise.nytarticlesearch.models.Article;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articles;
    private Context context;


    public ArticleAdapter(List<Article> articleList) {
        articles = articleList;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {

        Article article = articles.get(position);
        TextView title = holder.title;
        ImageView imageCover = holder.imageCover;
        final ProgressBar pbImage = holder.pbImage;
        title.setText(article.getHeadLine());
        //populate the thumbnail image

        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {

        /* Picasso.with(context).load(thumbnail)
                    .fit()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imageCover);*/
            // you could adjust its behavior by let Glide cache both the full-size image and the resized one with this command.
            pbImage.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(thumbnail)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pbImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageCover);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView imageCover;
        public ProgressBar pbImage;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.tvTitle);
            imageCover = (ImageView) v.findViewById(R.id.ivImage);
            pbImage = (ProgressBar) v.findViewById(R.id.pbImage);
            // Attach a click listener to the entire row view
            v.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            //get article display
            Article article = articles.get(getLayoutPosition());
            Intent i = new Intent(context, ArticleActivity.class);
            i.putExtra("article", article);
            context.startActivity(i);

        }
    }

}

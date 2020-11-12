package com.example.ligneactivite.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ligneactivite.Class.ArticleItem;
import com.example.ligneactivite.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArticleItemAdapter extends BaseAdapter {
  final private Context context;
    final private List<ArticleItem> articleItemsList;
   final private LayoutInflater inflater;

    public ArticleItemAdapter(Context context, List<ArticleItem> articleItemsList)
    {
        this.context = context;
        this.articleItemsList = articleItemsList;
        this.inflater = LayoutInflater.from(context);


    }

    @Override
    public int getCount() {
        return articleItemsList.size();
    }

    @Override
    public ArticleItem getItem(int position) { return articleItemsList.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_list,null);

        ArticleItem currentItem = getItem(position);
        String itemTitre = currentItem.getTitre();
        String itemDescription = currentItem.getDescrib();
        String itemUrl = currentItem.getUrl();
        String imageUrl = currentItem.getImageUrl();
        ImageView imageView = convertView.findViewById(R.id.icon_article);



        TextView itemTitleView = convertView.findViewById(R.id.nom_article);
        TextView itemDescrepView = convertView.findViewById(R.id.description_article);
        itemDescrepView.setText(itemDescription);
        itemTitleView.setText(itemTitre);
        LoadImage loadImage = new LoadImage(imageView);
        loadImage.execute(imageUrl);
        Button button_url = convertView.findViewById(R.id.Navigationr);

        if(!itemUrl.equals(""))
        {
            button_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked_button(itemUrl);

                }

            });

        }
        else
        {
            button_url.setVisibility(View.GONE);
        }



        return convertView;
    }
    public void clicked_button(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Un problème c'est produit", Toast.LENGTH_SHORT).show();

        }


    }


    @SuppressLint("StaticFieldLeak")
    private static class LoadImage extends AsyncTask<String,Void, Bitmap>
    {
        ImageView imageView;

        public LoadImage(ImageView imageView)
        {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlLink = strings[0];

            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlLink ).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}

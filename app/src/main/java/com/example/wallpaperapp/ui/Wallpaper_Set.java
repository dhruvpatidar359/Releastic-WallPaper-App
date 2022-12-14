package com.example.wallpaperapp.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wallpaperapp.R;
import com.example.wallpaperapp.db.DBHelper;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Wallpaper_Set extends AppCompatActivity {

    ImageView img;
    Button btn;
    TextView textView;
    ProgressBar progressBar;
    LikeButton likeButton;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_set);

        img = findViewById(R.id.preview_img);
        btn = findViewById(R.id.set_button);
        textView = findViewById(R.id.title_wallpaper);
        likeButton = findViewById(R.id.heart_button);
        likeButton.setEnabled(false);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);


        try {

            File fff = new File("/data/data/com.example.wallpaperapp/app_heavyImages", getIntent().getExtras().getString("id"));

            if (fff.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fff));
                progressBar.setVisibility(View.GONE);
                img.setImageBitmap(b);
                textView.setText(getIntent().getExtras().getString("imageName"));
                likeButton.setEnabled(true);
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                String isLiked = dbHelper.getFav(getIntent().getExtras().getString("id"));
                dbHelper.close();

                if (isLiked.equals("true")) {
                    likeButton.setLiked(true);
                }
            } else {

                Picasso.get().load(getIntent().getExtras().getString("URI")).into(picassoImageTarget(getApplicationContext(), "heavyImages", getIntent().getExtras().getString("imageName"), getIntent().getExtras().getString("id")));


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }


        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setLiked(true);
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.insertFav(getIntent().getExtras().getString("id"));
                dbHelper.close();

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeButton.setLiked(false);
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.removeFav((getIntent().getExtras().getString("id")));
                dbHelper.close();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             


                File ff = new File("/data/data/com.example.wallpaperapp/app_heavyImages", getIntent().getExtras().getString("id"));
                Bitmap b = null;
                try {
                    b = BitmapFactory.decodeStream(new FileInputStream(ff));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(b)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    WallpaperManager.getInstance(getApplicationContext()).setBitmap(resource);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

            }

        });

    }

    public Target picassoImageTarget(Context context, final String imageDir, final String imageName, String id) {

        ContextWrapper cw = new ContextWrapper(context);

        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE);
        final Target target =

                new Target() {

                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final File myImageFile = new File(directory, id); // Create image file

                                FileOutputStream fos = null;
                                try {

                                    fos = new FileOutputStream(myImageFile);

                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                } catch (IOException e) {
                                    e.printStackTrace();

                                } finally {
                                    try {
                                        fos.close();


                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {


                                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                                dbHelper.insertHeavy("/data/data/com.example.wallpaperapp/app_heavyImages", getIntent().getExtras().getString("id"));


                                                textView.setText(getIntent().getExtras().getString("imageName"));


                                                File ff = new File("/data/data/com.example.wallpaperapp/app_heavyImages", getIntent().getExtras().getString("id"));
                                                Bitmap b = null;
                                                try {
                                                    b = BitmapFactory.decodeStream(new FileInputStream(ff));
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                img.setImageBitmap(b);
                                                likeButton.setEnabled(true);

                                                String isLiked = dbHelper.getFav(getIntent().getExtras().getString("id"));

                                                if (isLiked.equals("true")) {
                                                    likeButton.setLiked(true);
                                                }
                                                dbHelper.close();

                                                progressBar.setVisibility(View.GONE); // to hide


                                            }
                                        });


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                            }
                        }).start();


                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        if (placeHolderDrawable != null) {
                        }
                    }
                };
        img.setTag(target);
        return target;


    }


}

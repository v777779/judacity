package ru.vpcb.ex_04_07;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ImageView imageView = findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.image_002);
        setAvatar();


        Transition move = TransitionInflater.from(this).inflateTransition(R.transition.move);
        getWindow().setSharedElementEnterTransition(move);


    }

    private void setAvatar() {

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.image_003);
        int size = Math.min(image.getWidth(), image.getHeight());
        Bitmap image_crop = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image_crop);
        canvas.drawBitmap(image, 0, 0, null);
        ImageView imageView = findViewById(R.id.image_view3);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), image_crop);
        drawable.setCornerRadius(size / 2);
        imageView.setImageDrawable(drawable);


    }
}

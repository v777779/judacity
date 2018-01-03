package ru.vpcb.ex_04_07;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {
    public static final int[] IMAGE_IDS = new int[]{R.drawable.image_001, R.drawable.image_002, R.drawable.image_003};
    public static final String BUNDLE_IMAGE_RESOURCE = "bundle_image_resource";

    private boolean isHero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView grid = findViewById(R.id.grid);
        grid.setAdapter(new GridAdapter(this));


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(MainActivity.this, DetailActivity.class),
//                        ActivityOptions.makeSceneTransitionAnimation(
//                                MainActivity.this,
//                                view,
//                                view.findViewById(R.id.image_view).getTransitionName()  // можно задать руками или взять из поля
//                        ).toBundle());
// with  bundle
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                        , view, "transition_photo").toBundle();

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(BUNDLE_IMAGE_RESOURCE, IMAGE_IDS[position % IMAGE_IDS.length]);
                startActivity(intent, bundle);

            }
        });

    }

    void onClickButton(View view) {

    }
}

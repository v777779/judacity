package ru.vpcb.bakingapp.data;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import ru.vpcb.bakingapp.R;

/**
 * Glide Configuration Class used to configure OkHttip client
 *
 */
@GlideModule
public class ConfigGlideModule extends AppGlideModule {
    /**
     * Setup connection options for OkHttp Client
     *
     * @param context Context of calling activity
     * @param glide Glide object
     * @param registry Registry object
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);

        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }

    /**
     * Setup download options for Glide client
     *
     * @param context Context of calling activity
     * @param builder GlideBuilder builder for Glide client
     */
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_play_circle_black_24dp)
                .error(R.drawable.not_available)
                .format(DecodeFormat.PREFER_ARGB_8888)
        );
    }
}
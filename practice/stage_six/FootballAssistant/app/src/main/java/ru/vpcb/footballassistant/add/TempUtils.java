package ru.vpcb.footballassistant.add;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 11-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class TempUtils {

    public static String readFileAssets(Context context, String fileName) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));

            // do reading, usually loop until end of file reading
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);

            }
            return sb.toString();
        } catch (IOException e) {
            //log the exception

            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }


    }
}

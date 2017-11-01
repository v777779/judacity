package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 01-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Emojifier {
    private static final String TAG = Emojifier.class.getSimpleName();

    public static void detectFaces(Context context, Bitmap bitmap) {
        int nFaces = 0;
        String message;

//        FaceDetector detector = new FaceDetector.Builder(context)
//                .setTrackingEnabled(false)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .build();

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        nFaces = faces.size();

        if (nFaces > 0) {
            message = nFaces+" faces detected";
            Log.d(TAG,"Number of detected faces: "+nFaces);
        }else {
           message = "No faces detected";
        }
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

        detector.release();
    }
}

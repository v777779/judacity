/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture) {

        // TODO (3): Change the name of the detectFacesAndOverlayEmoji() method to detectFacesAndOverlayEmoji() and the return type from void to Bitmap

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFacesAndOverlayEmoji: number of faces = " + faces.size());

        // TODO (7): Create a variable called resultBitmap and initialize it to the original picture bitmap passed into the detectFacesAndOverlayEmoji() method

        Bitmap resultBitmap = Bitmap.createBitmap(picture);

        // If there are no faces detected, show a Toast message
        if (faces.size() == 0) {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {

            // Iterate through the faces
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);
                // Get the appropriate emoji for each face


                // TODO (4): Create a variable called emojiBitmap to hold the appropriate Emoji bitmap and remove the call to whichEmoji()
                // TODO (5): Create a switch statement on the result of the whichEmoji() call, and assign the proper emoji bitmap to the variable you created
                // TODO (8): Call addBitmapToFace(), passing in the resultBitmap, the emojiBitmap and the Face  object, and assigning the result to resultBitmap

                int imageId = -1;
                switch (whichEmoji(face)) {
                    case SMILE:
                        imageId = R.drawable.smile;
                        break;
                    case LEFT_WINK:
                        imageId = R.drawable.leftwink;

                        break;
                    case RIGHT_WINK:
                        imageId = R.drawable.rightwink;

                        break;
                    case CLOSED_EYE_SMILE:
                        imageId = R.drawable.closed_smile;

                        break;
                    case FROWN:
                        imageId = R.drawable.frown;

                        break;
                    case LEFT_WINK_FROWN:
                        imageId = R.drawable.leftwinkfrown;

                        break;
                    case RIGHT_WINK_FROWN:
                        imageId = R.drawable.rightwinkfrown;

                        break;
                    case CLOSED_EYE_FROWN:
                        imageId = R.drawable.closed_frown;

                        break;
                    default:

                }
                if (imageId != -1) {
                    Bitmap emojiBitmap = BitmapFactory.decodeResource(context.getResources(), imageId);
                   resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
                }
            }
        }


        // Release the detector
        detector.release();
        // TODO (9): Return the resultBitmap
        return resultBitmap;
    }


    /**
     * Determines the closest emoji to the expression on the face, based on the
     * odds that the person is smiling and has each eye open.
     *
     * @param face The face for which you pick an emoji.
     */

    private static Emoji whichEmoji(Face face) {

        // TODO (1): Change the return type of the whichEmoji() method from void to Emoji.
        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());


        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;

        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;


        // Determine and log the appropriate emoji
        Emoji emoji;
        if (smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }


        // Log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: " + emoji.name());

        // TODO (2): Have the method return the selected Emoji type.
        return emoji;
    }

    // TODO (6) Create a method called addBitmapToFace() which takes the background bitmap,
    // the Emoji bitmap, and a Face object as arguments and returns the combined bitmap with the Emoji over the face.
    private static Bitmap addBitmapToFace(Bitmap backBitmap, Bitmap emojiBitmap, Face face) {
        PointF p = face.getPosition();
        Bitmap backCopy = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(backCopy);


        float faceScale = face.getHeight() / emojiBitmap.getHeight() * 0.5f;
        int faceWidth = (int) (emojiBitmap.getWidth() * faceScale);
        int faceHeight = (int) (emojiBitmap.getHeight() * faceScale);
        Bitmap emojiScaled = Bitmap.createScaledBitmap(emojiBitmap, faceWidth, faceHeight, false);

        float left = p.x - faceWidth;
        float top = p.y - faceHeight;
        Paint paint = new Paint();

//        int faceMinX = (int) (face.getPosition().x + face.getWidth());
//        int faceMinY = (int) (face.getPosition().y + face.getHeight());
//        int faceMaxX = (int) (face.getPosition().x);
//        int faceMaxY = (int) (face.getPosition().y);
//
//        for (Landmark landmark : face.getLandmarks()) {
//            int cx = (int) landmark.getPosition().x;
//            int cy = (int) landmark.getPosition().y;
//            if (cx > faceMaxX) faceMaxX = cx;
//            if (cy > faceMaxY) faceMaxY = cy;
//            if (cx < faceMinX) faceMinX = cx;
//            if (cy < faceMinY) faceMinY = cy;
//            paint.setColor(Color.BLUE);
//            canvas.drawCircle(cx, cy, 2, paint);
//
//        }
//
//        paint.setColor(Color.WHITE);
//
//        int cx = (int) (faceMinX + (float) (faceMaxX - faceMinX) / 2);
//        int cy = (int) (faceMinY + (float) (faceMaxY - faceMinY) / 2);
//        canvas.drawCircle(cx, cy, 5, paint);

        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == Landmark.NOSE_BASE) {
                int cx = (int) (landmark.getPosition().x - emojiScaled.getWidth()*0.5);
                int cy = (int) (landmark.getPosition().y - emojiScaled.getHeight()*0.75);
                canvas.drawBitmap(emojiScaled, cx, cy, null);
            }
        }

        return backCopy;
    }

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

}

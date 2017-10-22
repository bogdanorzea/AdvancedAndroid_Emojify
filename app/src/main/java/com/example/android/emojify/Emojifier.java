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
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap picture) {

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
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                // Log the classification probabilities for each face.
                whichEmoji(face);
            }

        }


        // Release the detector
        detector.release();
    }


    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static void whichEmoji(Face face){
        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        double smilingTreshold = 0.5;
        double leftEyeOpenedTreshold = 0.8;
        double rightEyeOpenedTreshold = 0.8;

        boolean isSmiling = face.getIsSmilingProbability() > smilingTreshold;
        boolean isLeftEyeOpened = face.getIsLeftEyeOpenProbability() > leftEyeOpenedTreshold;
        boolean isRightEyeOpened = face.getIsRightEyeOpenProbability() > rightEyeOpenedTreshold;

        if(isSmiling){
            if (isLeftEyeOpened){
                if (isRightEyeOpened){
                    Log.d(LOG_TAG, Emoji.SMILING.toString());
                } else {
                    Log.d(LOG_TAG, Emoji.RIGHT_WINK.toString());
                }
            } else {
                if (isRightEyeOpened){
                    Log.d(LOG_TAG, Emoji.LEFT_WINK.toString());
                } else {
                    Log.d(LOG_TAG, Emoji.CLOSED_EYE_SMILING.toString());
                }
            }
        } else {
            if (isLeftEyeOpened){
                if (isRightEyeOpened){
                    Log.d(LOG_TAG, Emoji.FROWING.toString());
                } else {
                    Log.d(LOG_TAG, Emoji.RIGHT_WINK_FROWNING.toString());
                }
            } else {
                if (isRightEyeOpened){
                    Log.d(LOG_TAG, Emoji.LEFT_WINK_FROWNING.toString());
                } else {
                    Log.d(LOG_TAG, Emoji.CLOSE_EYE_FROWNING.toString());
                }
            }
        }
    }

    enum Emoji {
        SMILING, LEFT_WINK, RIGHT_WINK, CLOSED_EYE_SMILING,
        FROWING, LEFT_WINK_FROWNING, RIGHT_WINK_FROWNING, CLOSE_EYE_FROWNING
    }
}

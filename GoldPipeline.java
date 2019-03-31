package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import org.firstinspires.ftc.teamcode.GoldMineral;

// LAST UPDATED: 12/27/18 \\
public class GoldPipeline extends OpenCVPipeline {
    private GoldMineral mGoldMineral;

    public GoldPipeline(GoldMineral goldMineral){
        mGoldMineral = goldMineral;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat rgb = inputFrame.rgba();
        Mat gray = inputFrame.gray();

        if(rgb == null)
            return null;

        if(mGoldMineral.getSearchArea() == null){
            mGoldMineral.setSearchArea(new Rect(
                    new Point((rgb.width() - 1) * 0.0,
                            (rgb.height() - 1) * 0.1),
                    new Point((rgb.width() - 1) * 1.0,
                            (rgb.height() - 1) * 0.4)));
        }

        mGoldMineral.locate(rgb, gray);

        return mGoldMineral.getAnnotatedImage();
    }

    @Override
    public Mat processFrame(Mat rgba, Mat gray){
        return null;
    }
}

package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})

// LAST UPDATE: 12/27/18 \\
public class GoldMineral {
    public static final double CenterMineral_Min = 0.3;
    public static final double CenterMineral_Max = 0.7;
    public static final int Sel_GoldMineral_MinArea = 2000;
    public static final int Cand_GoldMineral_MinArea =
            Sel_GoldMineral_MinArea / 2;

    public static final Scalar mask = new Scalar(255);
    final private static Scalar white = new Scalar(255, 255, 255);
    final private static Scalar green = new Scalar(0, 255, 0);
    final private static Scalar red = new Scalar(255, 0, 0);
    public static final Scalar GoldMineral_HSV_Min = new Scalar(10, 127, 80);
    public static final Scalar GoldMineral_HSV_Max = new Scalar(30, 255, 255);

    private Rect searchArea;
    private double currentGoldMineral_Area = 0;
    private double lastKnownGoldMineral_Area = 0;
    private MineralPosition currentGoldMineral_Pos = MineralPosition.UNKNOWN;
    private MineralPosition lastKnownMineral_Pos = MineralPosition.UNKNOWN;
    private Rect currentGoldMineral_BoundBox;
    private Rect lastKnownGoldMineral_BoundBox;
    private List<Rect> candGoldMineral_BoundBox;

    private Mat annotatedImage;

    private static void center_Label(Mat image, String label, Rect box, int verticalOffset){
        Size labelSize = Imgproc.getTextSize(label, 1, 1.0, 1, null);
        double offset = verticalOffset <= 0 ?
                box.tl().y + verticalOffset :
                box.br().y + labelSize.height + verticalOffset;
        Imgproc.putText(image, label,
                new Point(box.tl().x + (double) box.width / 2 - labelSize.width / 2, offset),
                1, 1.0, white, 1);

    }

    private MineralPosition derivePosition(Rect mineral_BoundBox){
        double mineralLocation =
                (double)(mineral_BoundBox.x - searchArea.x + mineral_BoundBox.width/2) /
                        (double) searchArea.width;

        if(mineralLocation < CenterMineral_Min)
            return MineralPosition.Left;
        else if(mineralLocation > CenterMineral_Max)
            return MineralPosition.Right;
        else
            return MineralPosition.Center;
    }

    public Rect getSearchArea(){
        return searchArea;
    }

    public void setSearchArea(Rect searchArea){
        this.searchArea = searchArea;
    }

    public MineralPosition getCurrentGoldMineral_Pos() {
        return currentGoldMineral_Pos;
    }

    public MineralPosition getLastKnownMineral_Pos() {
        return lastKnownMineral_Pos;
    }

    public Rect getCurrentGoldMineral_BoundBox(){
        return currentGoldMineral_BoundBox;
    }

    public Rect getLastKnownGoldMineral_BoundBox(){
        return lastKnownGoldMineral_BoundBox;
    }

    public List<Rect> getCandGoldMineral_BoundBox(){
        return candGoldMineral_BoundBox;
    }

    public Mat getAnnotatedImage(){
        return annotatedImage;
    }

    private Mat annotateImage(Mat rgbaImage, Mat grayImage, Mat goldMineralMask){
        grayImage.copyTo(annotatedImage);
        Imgproc.cvtColor(annotatedImage, annotatedImage, Imgproc.COLOR_GRAY2RGBA, 4);

        rgbaImage.copyTo(annotatedImage, goldMineralMask);

        if(searchArea != null){
            Imgproc.rectangle(annotatedImage, searchArea.tl(), searchArea.br(), green, 2);
        }

        if(candGoldMineral_BoundBox != null){
            for(Rect candGoldMineral_BoundBox : candGoldMineral_BoundBox){
                Imgproc.rectangle(annotatedImage,
                        candGoldMineral_BoundBox.tl(), candGoldMineral_BoundBox.br(),
                        red, 1);
            }
        }

        if(currentGoldMineral_BoundBox != null){
            Imgproc.rectangle(annotatedImage,
                    currentGoldMineral_BoundBox.tl(), currentGoldMineral_BoundBox.br(),
                    white, 4);
            center_Label(annotatedImage,
                    currentGoldMineral_Pos.toString(),
                    currentGoldMineral_BoundBox, -5);

            center_Label(annotatedImage,
                    String.format(Locale.US, "%d, %d",
                            currentGoldMineral_BoundBox.x + currentGoldMineral_BoundBox.width / 2,
                            currentGoldMineral_BoundBox.y + currentGoldMineral_BoundBox.height / 2),
                    currentGoldMineral_BoundBox, +5);

            center_Label(annotatedImage,
                    String.format(Locale.US, "%.0f / %.0f",
                            currentGoldMineral_Area, currentGoldMineral_BoundBox.area()),
                    currentGoldMineral_BoundBox, +20);
        }

        return annotatedImage;
    }

    public Mat getOriginalImage(){
        return originalImage;
    }

    private Mat originalImage;
    private Mat HSVImage;
    private Mat searchedImage;
    private Mat goldMineralMask;
    public boolean locate(Mat rgbaImage, Mat grayImage){
        if(originalImage == null)
            originalImage = new Mat();
        if(HSVImage == null)
            HSVImage = new Mat();
        if(searchedImage == null)
            searchedImage = new Mat();
        if(goldMineralMask == null)
            goldMineralMask = new Mat();
        if(annotatedImage == null)
            annotatedImage = new Mat();

        rgbaImage.copyTo(originalImage);

        Imgproc.cvtColor(rgbaImage, HSVImage, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(HSVImage, HSVImage, Imgproc.COLOR_RGB2HSV);

        if(searchArea != null){
            Mat searchAreaMask = Mat.zeros(rgbaImage.size(), CvType.CV_8U);
            Imgproc.rectangle(searchAreaMask, searchArea.tl(), searchArea.br(), mask, -1);
            HSVImage.copyTo(searchedImage);
        } else{
            HSVImage.copyTo(searchedImage);
        }

        Core.inRange(searchedImage, GoldMineral_HSV_Min, GoldMineral_HSV_Max, goldMineralMask);

        List<MatOfPoint> goldMineralContours = new ArrayList<>();
        Imgproc.findContours(goldMineralMask, goldMineralContours, new Mat(),
                Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        currentGoldMineral_BoundBox = null;
        currentGoldMineral_Pos = MineralPosition.UNKNOWN;

        if(!goldMineralContours.isEmpty()){
            MatOfPoint selectedContour = Collections.max(goldMineralContours, new Comparator<MatOfPoint>(){
                @Override
                public int compare(MatOfPoint o1, MatOfPoint o2){
                    return Double.compare(
                            Imgproc.contourArea(o1),
                            Imgproc.contourArea(o2));
                }
            });

            Rect selectedContourBoundingBox = Imgproc.boundingRect(selectedContour);
            double selectedContourArea = Imgproc.contourArea(selectedContour);
            if(selectedContourArea >= Sel_GoldMineral_MinArea){
                currentGoldMineral_Area = selectedContourArea;
                currentGoldMineral_BoundBox = selectedContourBoundingBox;
                currentGoldMineral_Pos = derivePosition(currentGoldMineral_BoundBox);
            }

            candGoldMineral_BoundBox = new ArrayList<>();
            for(MatOfPoint candidateMineralContour : goldMineralContours){
                Rect bb = Imgproc.boundingRect(candidateMineralContour);
                if(bb.area() > Cand_GoldMineral_MinArea &&
                        !selectedContourBoundingBox.contains(bb.tl()) &&
                        !selectedContourBoundingBox.contains(bb.br()) &&
                        !selectedContourBoundingBox.contains(new Point(bb.tl().x, bb.br().y)) &&
                        !selectedContourBoundingBox.contains(new Point(bb.br().x, bb.tl().y)))
                    candGoldMineral_BoundBox.add(bb);
            }
        }

        if(currentGoldMineral_Pos != MineralPosition.UNKNOWN){
            lastKnownGoldMineral_Area = currentGoldMineral_Area;
            lastKnownMineral_Pos = currentGoldMineral_Pos;
            lastKnownGoldMineral_BoundBox = currentGoldMineral_BoundBox;
        }

        annotatedImage = annotateImage(originalImage, grayImage, goldMineralMask);

        return currentGoldMineral_Pos != MineralPosition.UNKNOWN;
    }

    public enum MineralPosition {
        UNKNOWN,
        Left,
        Center,
        Right,
    }
}

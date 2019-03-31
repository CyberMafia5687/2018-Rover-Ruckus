package org.firstinspires.ftc.teamcode;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.firstinspires.ftc.teamcode.GoldMineral;
import org.firstinspires.ftc.teamcode.GoldPipeline;

// LAST UPDATED: 12/26/18 \\
@TeleOp
@SuppressWarnings({"unused"})
public class TESTGoldMineral extends LinearOpMode {
    final private File SavedImagePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    final private SimpleDateFormat SavedImageDateFormat =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private static void saveCapturedImage(File path, String prefix, Mat rgbaImage, String suffix){
        Mat bgrImage = new Mat();
        Imgproc.cvtColor(rgbaImage, bgrImage, Imgproc.COLOR_RGBA2BGR, 3);
        String filename = prefix + "_" + suffix + ".jpg";
        File file = new File(path, filename);
        Imgcodecs.imwrite(file.toString(), bgrImage);
    }

    @Override
    public void runOpMode(){
        GoldMineral goldMineral = new GoldMineral();
        GoldPipeline goldPipeline = new GoldPipeline(goldMineral);
        goldPipeline.init(hardwareMap.appContext,
                CameraViewDisplay.getInstance());
        goldPipeline.enable();

        while(!isStarted() && !isStopRequested()){
            telemetry.addData("Current Position",
                    goldMineral.getCurrentGoldMineral_Pos());
            telemetry.addData("Last Known Position",
                    goldMineral.getLastKnownMineral_Pos());
            telemetry.update();
        }

        goldPipeline.disable();

        String savedImagePrefix = SavedImageDateFormat.format(new Date());

        saveCapturedImage(SavedImagePath, savedImagePrefix,
                goldMineral.getOriginalImage(),
                goldMineral.getCurrentGoldMineral_Pos().
                        toString().toLowerCase());

        saveCapturedImage(SavedImagePath, savedImagePrefix,
                goldMineral.getAnnotatedImage(),
                "annotated");
    }
}

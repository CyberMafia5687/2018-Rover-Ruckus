package org.firstinspires.ftc.teamcode;

// Main Imports

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// Extra Imports
import android.os.Environment;

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

// LAST UPDATED: 1/14/19 \\
// TEMPORARY PROGRAM  ....  USE: TO TEST GOLD MINERAL IN AUTO //
@Autonomous
// @Disabled
public class TESTAuto extends LinearOpMode {

    // Motors
    private DcMotor liftMotor = null;

    // Servos
    private Servo markerServo;

    // Sensors
    private BNO055IMU imu;
    // private DistanceSensor sensorRange;
    // DigitalChannel digitalMagLimit;

    // Other
    GoldMineral goldMineralLocator;
    GoldPipeline goldMineralPipeline;

    final private File SAVED_IMAGE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    final private SimpleDateFormat SAVED_IMAGE_DATE_FORMAT =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private static void saveCapturedImage(File path, String prefix, Mat rgbaImage, String suffix){
        Mat bgrImage = new Mat();
        Imgproc.cvtColor(rgbaImage, bgrImage, Imgproc.COLOR_RGBA2BGR);
        String filename = prefix + "_" + suffix + ".jpg";
        File file = new File(path, filename);
        Imgcodecs.imwrite(file.toString(), bgrImage);
    }


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Running");
        telemetry.update();

        // Motors
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);

        // Servos
        markerServo = hardwareMap.get(Servo.class, "markerServo");

        // Sensors
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        // sensorRange = hardwareMap.get(DistanceSensor.class, "sensor_range");

        // digitalMagLimit = hardwareMap.get(DigitalChannel.class, "sensor_mag_limit");
        // digitalMagLimit.setMode(DigitalChannel.Mode.INPUT);

        // Other
        goldMineralLocator = new GoldMineral();
        goldMineralPipeline = new GoldPipeline(goldMineralLocator);
        goldMineralPipeline.init(hardwareMap.appContext,
                CameraViewDisplay.getInstance());
        goldMineralPipeline.enable();

        //waitForStart();
        while (!opModeIsActive() && !isStopRequested()){
            telemetry.addData("Status", "Waiting for Start Command...");
            telemetry.update();
        }

        // Gold Mineral Program
        goldMineralPipeline.disable();
        String savedImagePrefix = SAVED_IMAGE_DATE_FORMAT.format(new Date());

        saveCapturedImage(SAVED_IMAGE_PATH, savedImagePrefix,
                goldMineralLocator.getOriginalImage(),
                goldMineralLocator.getCurrentGoldMineral_Pos().toString().toLowerCase());

        saveCapturedImage(SAVED_IMAGE_PATH, savedImagePrefix,
                goldMineralLocator.getAnnotatedImage(),"annoted");

        // Landing v1 w/ Adjustments
        if (opModeIsActive()) {
            liftMotor.setPower(-0.65);
            sleep(3500);
            liftMotor.setPower(0);
        }

        /*
        // Landing v2 (BETA)
        if (digitalMagLimit.getState()){
            liftMotor.setPower(-0.4);
        } else{
            liftMotor.setPower(0);
        }
        */
    }
}
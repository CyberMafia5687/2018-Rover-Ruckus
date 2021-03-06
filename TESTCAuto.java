package org.firstinspires.ftc.teamcode;

// Main Imports

import android.os.Environment;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Extra Imports

// LAST UPDATED: 2/6/19 \\
// TEMPORARY PROGRAM  ....  USE: TO TEST DEPOT PROGRAM, STARTING FROM CRATER SIDE //
@Autonomous
// @Disabled
public class TESTCAuto extends LinearOpMode {

    // Motors
    private DcMotor frontLeftMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backRightMotor = null;
    private DcMotor liftMotor = null;
    private DcMotor armMotor = null;

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

        // Motors
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);

        // Servos
        markerServo = hardwareMap.get(Servo.class, "markerServo");

        // Sensors
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        Orientation orientation = imu.getAngularOrientation();

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
            telemetry.addData("Status", "Waiting for  next mission...");
            telemetry.update();
        }

        // Landing v1 w/ Adjustments
        if (opModeIsActive()) {

            markerServo.setPosition(0);

            // Detach from Lander
            liftMotor.setPower(-0.65);
            sleep(4550);
            liftMotor.setPower(0);

            // Raise Arm for Phone
            armMotor.setPower(-0.6);
            sleep(3500);
            armMotor.setPower(0);

            // Gold Locating (Gold Mineral Program)
            telemetry.addData("Status", "HitBot is acquiring mineral...");
            telemetry.update();

            goldMineralPipeline.disable();
            String savedImagePrefix = SAVED_IMAGE_DATE_FORMAT.format(new Date());

            saveCapturedImage(SAVED_IMAGE_PATH, savedImagePrefix,
                    goldMineralLocator.getOriginalImage(),
                    goldMineralLocator.getCurrentGoldMineral_Pos().toString().toLowerCase());

            saveCapturedImage(SAVED_IMAGE_PATH, savedImagePrefix,
                    goldMineralLocator.getAnnotatedImage(),"annoted");

            // Gold Decisions
            GoldMineral.MineralPosition mineralPosition =
                    goldMineralLocator.getLastKnownMineral_Pos();

            ////////////

            telemetry.addData("Status", "HitBot is Running");
            telemetry.update();

            // Put Arm Back Down
            armMotor.setPower(0.4);
            sleep(2000);
            armMotor.setPower(0);


            // Center/Unknown, Left, Right
            if(mineralPosition == GoldMineral.MineralPosition.UNKNOWN ||
                    mineralPosition == GoldMineral.MineralPosition.Center){
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Put lift back down
                liftMotor.setPower(0.65);
                sleep(4550);
                liftMotor.setPower(0);

                // Back up from Crater
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(1250);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Spin Left
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to wall
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(1000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Spin Left (Parallel to wall) + NOT touching wall
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to Depot
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Deposit Marker
                markerServo.setPosition(1);

                // Reverse to Crater and Park
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(3000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                /////
            } else if(mineralPosition == GoldMineral.MineralPosition.Left){
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(400);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                // Turn Left
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(800);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                //
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(1200);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Put lift back down
                liftMotor.setPower(0.65);
                sleep(4550);
                liftMotor.setPower(0);

                // Spin Left
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to wall (NOT touching wall)
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Spin parallel to wall
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to Depot
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Deposit Marker
                markerServo.setPosition(1);

                // Reverse and park in Crater
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                /////
            } else if(mineralPosition == GoldMineral.MineralPosition.Right){
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(400);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                // Turn Right
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                sleep(800);
                frontLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                //
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(1200);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Put lift back down
                liftMotor.setPower(0.65);
                sleep(4550);
                liftMotor.setPower(0);

                // Reverse to original landing spot
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(1200);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Spin Left
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(1000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to wall
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Spin parallel to wall (NOT touching wall)
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Move to Depot
                frontLeftMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Deposit Marker
                markerServo.setPosition(1);

                // Reverse and park in Crater
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                /////
            }

        }
    }
}
package org.firstinspires.ftc.teamcode;

// Main Imports

import android.os.Environment;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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

// LAST UPDATED: 4/14/19 \\
@Autonomous
// @Disabled
public class EDEPOTAuto extends LinearOpMode {

    // Motors
    private DcMotor frontLeftMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backRightMotor = null;
    private DcMotor liftMotor = null;
    private DcMotor armMotor = null;

    private ElapsedTime runtime = new ElapsedTime();

    private int driveMotorCalculate(double degrees){
        return(int)(degrees * Constants.HITBOT_DRIVEMOTOR_COUNTS_DEGREE);
    }

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

    private int liftDistance(double mm){
        return (int)(mm * (383.6 / 8));
    }

    @Override
    public void runOpMode() throws InterruptedException {

        // Motors
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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
        runtime.reset();

        // Landing v1 w/ Adjustments
        if (opModeIsActive()) {

            markerServo.setPosition(0);

            // Detach from Lander
            liftMotor.setTargetPosition(liftDistance(215));
            liftMotor.setPower(0.7);
            ElapsedTime t = new ElapsedTime();
            while (t.milliseconds() < 6500) {
                telemetry.addData("lift pos", liftMotor.getCurrentPosition());
                telemetry.update();
            }
            liftMotor.setPower(0);

            // Raise Arm for Phone
            armMotor.setTargetPosition(-240);
            armMotor.setPower(0.5);
            ElapsedTime r = new ElapsedTime();
            while (armMotor.getCurrentPosition() > -200) {
                telemetry.addData("Status", "The HitBot is acquiring the mineral");
                telemetry.update();
            }

            sleep(400);

            // Gold Locating (Gold Mineral Program)
            telemetry.addData("Status", "Mineral Acquired");
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

            telemetry.addData("Current Position:", goldMineralLocator.getCurrentGoldMineral_Pos());
            telemetry.update();

            // Put Arm Back Down
            sleep(200);
            armMotor.setTargetPosition(-5);
            armMotor.setPower(0.5);

            frontLeftMotor.setTargetPosition(288);
            backLeftMotor.setTargetPosition(288);
            frontRightMotor.setTargetPosition(288);
            backRightMotor.setTargetPosition(288);
            frontLeftMotor.setPower(0.5);
            backLeftMotor.setPower(0.5);
            frontRightMotor.setPower(0.5);
            backRightMotor.setPower(0.5);
            sleep(1500);
            frontLeftMotor.setPower(0);
            backLeftMotor.setPower(0);
            frontRightMotor.setPower(0);
            backRightMotor.setPower(0);
            sleep(200);


            // Center/Unknown, Left, Right
            if(mineralPosition == GoldMineral.MineralPosition.UNKNOWN ||
                    mineralPosition == GoldMineral.MineralPosition.Center){
                frontLeftMotor.setTargetPosition(940);
                backLeftMotor.setTargetPosition(940);
                frontRightMotor.setTargetPosition(940);
                backRightMotor.setTargetPosition(940);
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(4000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(288 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(288 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                // Deposit Marker
                markerServo.setPosition(0.75);
                sleep(900);
                markerServo.setPosition(0);

                frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(315 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(315 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(2500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                frontLeftMotor.setTargetPosition(1200 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(1200 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(1200 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(1200 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.7);
                backLeftMotor.setPower(0.7);
                frontRightMotor.setPower(0.7);
                backRightMotor.setPower(0.7);
                sleep(5000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                /////
            } else if(mineralPosition == GoldMineral.MineralPosition.Left){
                frontLeftMotor.setTargetPosition(-72 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(-72 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(72 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(72 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(1000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);
                //
                frontLeftMotor.setTargetPosition(750 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(750 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(750 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(750 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(2450);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(1090 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(1090 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(2480);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(300);

                frontLeftMotor.setTargetPosition(-700 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(-700 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(-700 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(-700 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.7);
                backLeftMotor.setPower(0.7);
                frontRightMotor.setPower(0.7);
                backRightMotor.setPower(0.7);
                sleep(2700);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Deposit Marker
                markerServo.setPosition(0.75);
                sleep(900);
                markerServo.setPosition(0);

                frontLeftMotor.setTargetPosition(1500 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(1500 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(1500 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(1500 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.9);
                backLeftMotor.setPower(0.9);
                frontRightMotor.setPower(0.9);
                backRightMotor.setPower(0.9);
                sleep(6100);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                /////
            } else if(mineralPosition == GoldMineral.MineralPosition.Right){
                frontLeftMotor.setTargetPosition(72 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(72 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(-72 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(-72 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(1000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);
                //
                frontLeftMotor.setTargetPosition(600 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(600 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(600 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(600 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.7);
                backLeftMotor.setPower(0.7);
                frontRightMotor.setPower(0.7);
                backRightMotor.setPower(0.7);
                sleep(2900);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(570 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(570 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(2800);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                frontLeftMotor.setTargetPosition(180 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(180 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(180 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(180 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.6);
                backLeftMotor.setPower(0.6);
                frontRightMotor.setPower(0.6);
                backRightMotor.setPower(0.6);
                sleep(2500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                // Deposit Marker
                markerServo.setPosition(0.75);
                sleep(900);
                markerServo.setPosition(0);

                frontLeftMotor.setTargetPosition(240 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(240 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(240 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(240 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.7);
                backLeftMotor.setPower(0.7);
                frontRightMotor.setPower(0.7);
                backRightMotor.setPower(0.7);
                sleep(2500);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

                frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(330 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(330 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(1100);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                frontLeftMotor.setTargetPosition(1130 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(1130 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(1130 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(1130 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.8);
                backLeftMotor.setPower(0.8);
                frontRightMotor.setPower(0.8);
                backRightMotor.setPower(0.8);
                sleep(5000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                sleep(200);

                /////
            }

        }
    }
}

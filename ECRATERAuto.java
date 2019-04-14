package org.firstinspires.ftc.teamcode;

// Main Imports
import android.os.Environment;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
public class ECRATERAuto extends LinearOpMode {

    // Motors
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private DcMotor liftMotor;
    private DcMotor armMotor;

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
                telemetry.addData("Status", "The HitBot is acquiring the mineral...");
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

            // Variable for Determining Left or Right turn
            int x = 1;
            if(mineralPosition == GoldMineral.MineralPosition.Right){
                x = -1;
            }

            // Center/Unknown, Left, Right
            if(mineralPosition == GoldMineral.MineralPosition.UNKNOWN ||
                    mineralPosition == GoldMineral.MineralPosition.Center){
                frontLeftMotor.setTargetPosition(800);
                backLeftMotor.setTargetPosition(800);
                frontRightMotor.setTargetPosition(800);
                backRightMotor.setTargetPosition(800);
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(4000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);

            } else if(mineralPosition == GoldMineral.MineralPosition.Left || mineralPosition == GoldMineral.MineralPosition.Right){
                //
                sleep(500);
                frontLeftMotor.setTargetPosition(x * -72 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(x * -72 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(x * 72 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(x * 72 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(2000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
                //
                frontLeftMotor.setTargetPosition(550 + frontLeftMotor.getCurrentPosition());
                backLeftMotor.setTargetPosition(550 + backLeftMotor.getCurrentPosition());
                frontRightMotor.setTargetPosition(550 + frontRightMotor.getCurrentPosition());
                backRightMotor.setTargetPosition(550 + backRightMotor.getCurrentPosition());
                frontLeftMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                sleep(4000);
                frontLeftMotor.setPower(0);
                backLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backRightMotor.setPower(0);
            }

            // Put lift back down
            liftMotor.setTargetPosition(liftDistance(0));
            liftMotor.setPower(0.7);
            ElapsedTime e = new ElapsedTime();
            while (e.milliseconds() < 6500) {
                telemetry.addData("lift pos", liftMotor.getCurrentPosition());
                telemetry.update();
            }
            liftMotor.setPower(0);

        }
    }
}

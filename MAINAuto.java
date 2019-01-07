package org.firstinspires.ftc.teamcode;

// Main Imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

// Extra Imports
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

// LAST UPDATED: 1/7/19 \\
@Autonomous
// @Disabled
public class MAINAuto extends LinearOpMode {

    // Motors
    private DcMotor liftMotor = null;

    // Servos
    //

    // Sensors
    private BNO055IMU imu;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Running");
        telemetry.update();

        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);

        // Sensors
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        //waitForStart();
        while (!opModeIsActive() && !isStopRequested()){
            telemetry.addData("Status", "Waiting for Start Command...");
            telemetry.update();
        }

        // Landing
        if (opModeIsActive()) {
            liftMotor.setPower(-0.75);
            sleep(2600);
            liftMotor.setPower(0);
        }
    }
}

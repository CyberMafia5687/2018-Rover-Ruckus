package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
@Disabled
public class OmniRevRuckus extends LinearOpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor liftMotor;



    @Override
    public void runOpMode() {

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");



        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for game to start (Driver, press PLAY)
        waitForStart();

        // Run until end of match (Driver, press STOP)
        double tgtPower1 = 0;
        double tgtPower2 = 0;
        double tgtPower3 = 0;
        double tgtPower4 = 0;
        double tgtPower5 = 0;


        while (opModeIsActive()){

           // Tank Drive
           tgtPower1 = this.gamepad1.left_stick_y;
           frontLeftMotor.setPower(tgtPower1);
           backLeftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           frontRightMotor.setPower(tgtPower2);
           backRightMotor.setPower(tgtPower2);

           // Strafe Drive
           tgtPower3 = this.gamepad1.left_stick_x;
           frontLeftMotor.setPower(tgtPower3);
           backLeftMotor.setPower(-tgtPower3);

           tgtPower4 = this.gamepad1.right_stick_x;
           frontRightMotor.setPower(tgtPower4);
           backRightMotor.setPower(-tgtPower4);


           tgtPower5 = this.gamepad1.right_trigger;
           liftMotor.setPower(tgtPower5);

           tgtPower5 = this.gamepad1.left_trigger;
           liftMotor.setPower(-tgtPower5);
           }

           telemetry.addData("Status", "Running");
           telemetry.update();
        }
    }

// http://www.revrobotics.com/content/docs/REV-31-1153-GS.pdf
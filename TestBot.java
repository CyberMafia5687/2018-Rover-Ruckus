package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


// LAST UPDATED: 12/16/18 \\

// TO BE USED WITH TETRIX TEST ROBOT ONLY\\
@TeleOp
// @Disabled
public class TestBot extends LinearOpMode {
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor armMotor1;
    private DcMotor armMotor2;


    @Override
    public void runOpMode() {

        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        armMotor1 = hardwareMap.get(DcMotor.class, "armMotor1");
        armMotor2 = hardwareMap.get(DcMotor.class, "armMotor2");

        telemetry.addData("Status", "Running");
        telemetry.update();


        // Wait for game to start (Driver, press PLAY)
        // waitForStart();
        while (!opModeIsActive() && !isStopRequested()){
            telemetry.addData("Status", "Waiting for Start Command...");
            telemetry.update();
        }

        // Run until end of match (Driver, press STOP)
        double tgtPower1 = 0;
        double tgtPower2 = 0;
        double tgtPower3 = 0;

        while (opModeIsActive()){

           // Tank Drive
           tgtPower1 = this.gamepad1.left_stick_y;
           leftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           rightMotor.setPower(tgtPower2);

           tgtPower3 = this.gamepad1.left_trigger;
           armMotor1.setPower(tgtPower3);
           armMotor2.setPower(-tgtPower3);

           tgtPower3 = this.gamepad1.right_trigger;
           armMotor1.setPower(-tgtPower3);
           armMotor2.setPower(tgtPower3);

           }

           telemetry.addData("Status", "Running");
           telemetry.update();
        }
    }

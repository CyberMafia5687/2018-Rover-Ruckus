package org.firstinspires.ftc.teamcode;

// Main Imports
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// Extra Imports
import com.qualcomm.robotcore.hardware.DigitalChannel;


// LAST UPDATED: 1/7/19 \\
@TeleOp
// @Disabled
public class MAINRev extends LinearOpMode {

    // Motors
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor liftMotor;
    private DcMotor armMotor;
    private DcMotor sweepMotor;

    // Servos
    private Servo markerServo;

    // Sensors
    DigitalChannel digitalMagLimit;


    @Override
    public void runOpMode() {

        leftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        sweepMotor = hardwareMap.get(DcMotor.class, "sweepMotor");

        markerServo = hardwareMap.get(Servo.class, "markerServo");

        telemetry.addData("Status", "Initializing...");
        telemetry.update();


        // Wait for game to start (Driver, press PLAY)
        // waitForStart();
        while (!opModeIsActive() && !isStopRequested()){
            telemetry.addData("Status", "Waiting for Start Command");
            telemetry.update();
        }

        // Run until end of match (End of match, press STOP)
        double tgtPower1 = 0;
        double tgtPower2 = 0;
        double tgtPower3 = 0;
        double tgtPower4 = 0;
        double tgtPower5 = 0;

        while (opModeIsActive()){

           // Tank Drive
           tgtPower1 = this.gamepad1.left_stick_y;
           leftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           rightMotor.setPower(tgtPower2);

           // Moon Lander Lift Control
           if (this.gamepad1.dpad_up){
               liftMotor.setPower(-0.5);
               sleep(3230);
               liftMotor.setPower(0);
           } else if (this.gamepad1.dpad_down){
               liftMotor.setPower(0.55);
               sleep(3250);
               liftMotor.setPower(0);
           }

           /*
           else if (this.gamepad1.x){
               break;
           }
           */

           /*
           while (true){
               if (this.gamepad1.dpad_up){
                   liftMotor.setPower(-0.5);
                   sleep(3230);
                   liftMotor.setPower(0);
               } else if (this.gamepad1.dpad_down){
                   liftMotor.setPower(0.55);
                   sleep(3250);
                   liftMotor.setPower(0);
               } else if (this.gamepad1.x){
                   break;
               }
           }
           */

           // Precision Lift Controls
           tgtPower3 = this.gamepad2.right_trigger;
           liftMotor.setPower(tgtPower3);
           tgtPower3 = this.gamepad2.left_trigger;
           liftMotor.setPower(-tgtPower3);

           // Arm Control
           tgtPower4 = this.gamepad2.left_stick_y;
           armMotor.setPower(-tgtPower4);

           // Sweeper Controls
           if(gamepad2.left_bumper){
               sweepMotor.setPower(tgtPower5);
           } else if(gamepad2.right_bumper){
               sweepMotor.setPower(-tgtPower5);
           }

           }

           telemetry.addData("Status", "Running");
           telemetry.update();
        }
    }

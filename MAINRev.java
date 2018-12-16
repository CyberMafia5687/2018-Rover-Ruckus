package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


// LAST UPDATED: 12/16/18 \\
@TeleOp
// @Disabled
public class MAINRev extends LinearOpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor liftMotor;
    private DcMotor armMotor;

    public Servo handServo = null;
    public CRServo sweepServo;


    @Override
    public void runOpMode() {

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");

        handServo = hardwareMap.get(Servo.class, "handServo");
        sweepServo = hardwareMap.get(CRServo.class, "sweepServo");

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
        double tgtPower4 = 0;
        double tgtPower5 = 0;
        double tgtPower6 = 0;

        while (opModeIsActive()){

           // Tank Drive
           tgtPower1 = this.gamepad1.left_stick_y;
           frontLeftMotor.setPower(tgtPower1);
           backLeftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           frontRightMotor.setPower(tgtPower2);
           backRightMotor.setPower(tgtPower2);

           // Strafe Drive
            /*
           tgtPower3 = this.gamepad1.left_stick_x;
           frontLeftMotor.setPower(tgtPower3);
           backLeftMotor.setPower(-tgtPower3);

           tgtPower4 = this.gamepad1.right_stick_x;
           frontRightMotor.setPower(tgtPower4);
           backRightMotor.setPower(-tgtPower4);
           */

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
           tgtPower5 = this.gamepad2.right_trigger;
           liftMotor.setPower(tgtPower5);
           tgtPower5 = this.gamepad2.left_trigger;
           liftMotor.setPower(-tgtPower5);

           // Arm Control
           tgtPower6 = this.gamepad2.left_stick_y;
           armMotor.setPower(-tgtPower6);

           // Sweeper Controls
           if(gamepad2.left_bumper){
               sweepServo.setPower(10.0);
           } else if(gamepad2.right_bumper){
               sweepServo.setPower(-10.0);
           } else{
               sweepServo.setPower(0);
           }

           // Bucket Controls
           if (gamepad2.y){
               handServo.setPosition(1);
           } else if (gamepad2.x){
               handServo.setPosition(0.46);
           } else{
               handServo.setPosition(0);
           }

           }

           telemetry.addData("Status", "Running");
           telemetry.update();
        }
    }

// http://www.revrobotics.com/content/docs/REV-31-1153-GS.pdf
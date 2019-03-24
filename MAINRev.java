package org.firstinspires.ftc.teamcode;

// Main Imports
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// Extra Imports
// import com.qualcomm.robotcore.hardware.DigitalChannel;


// LAST UPDATED: 1/30/19 \\
@TeleOp
// @Disabled
public class MAINRev extends LinearOpMode {

    // Motors
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private DcMotor liftMotor;
    private DcMotor armMotor;
    private DcMotor sweepMotor;

    // Servos

    // Sensors
    // DigitalChannel digitalMagLimit;

    // Encoder Command
    //


    @Override
    public void runOpMode() {

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        /*
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        */

        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        sweepMotor = hardwareMap.get(DcMotor.class, "sweepMotor");

        /*
        digitalMagLimit = hardwareMap.get(DigitalChannel.class, "sensorMagLimit");
        digitalMagLimit.setMode(DigitalChannel.Mode.INPUT);
        */


        // Wait for game to start (Driver, press PLAY)
        // waitForStart();
        while (!opModeIsActive() && !isStopRequested()){
            telemetry.addData("Status", "Waiting for  next mission...");
            telemetry.update();
        }

        // Run until end of match (End of match, press STOP)
        double tgtPower1 = 0;
        double tgtPower2 = 0;
        double tgtPower3 = 0;
        double tgtPower4 = 0;
        double tgtPower5 = 0;

        while (opModeIsActive()){

           telemetry.addData("Status", "HitBot is Running");
           telemetry.update();

           // Tank Drive
           tgtPower1 = this.gamepad1.left_stick_y;
           frontLeftMotor.setPower(tgtPower1);
           backLeftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           frontRightMotor.setPower(tgtPower2);
           backRightMotor.setPower(tgtPower2);

           // Tank Drive, Controlled v1
           if(this.gamepad1.right_bumper){
               frontLeftMotor.setPower(tgtPower1 / 8);
               backLeftMotor.setPower(tgtPower1 / 8);
               frontRightMotor.setPower(tgtPower2 / 8);
               backRightMotor.setPower(tgtPower2 / 8);
           }


           /*
           // Moon Lander Lift Control v1
           if (this.gamepad1.dpad_up){
               liftMotor.setPower(-0.5);
               sleep(3230);
               liftMotor.setPower(0);
           } else if (this.gamepad1.dpad_down){
               liftMotor.setPower(0.55);
               sleep(3250);
               liftMotor.setPower(0);
           }
           */

           /*
           // Moon Lander Lift Control v2 (BETA)
           while (digitalMagLimit.getState()){
               if (this.gamepad1.dpad_up){
                   liftMotor.setPower(-0.4);
               } else if (this.gamepad1.dpad_down){
                   liftMotor.setPower(0.4);
               } else{
                   liftMotor.setPower(0);
               }
           }

           // Moon Lander Lift Control v3 (BETA)
            if (this.gamepad1.y){
                liftMotor.setPower(-0.4);
                sleep(500);
                liftMotor.setPower(0);
            }
            if (this.gamepad1.dpad_up && digitalMagLimit.getState()){
                liftMotor.setPower(-0.4);
            } else if (this.gamepad1.dpad_down && digitalMagLimit.getState()){
                liftMotor.setPower(0.4);
            } else{
               liftMotor.setPower(0);
            }

            */

           /*
           // Moon Lander Lift Control v4 (BETA)
           while (digitalMagLimit.getState()){
               if (this.gamepad1.dpad_up){
                   liftMotor.setPower(0.5);
               } else if(this.gamepad1.dpad_down){
                   liftMotor.setPower(0.5);
               } else{
                   liftMotor.setPower(0);
               }
           }
           */

           // TODO New Lift Mechanism
           // Moon Lander Lift Control v5 (with Jeremy)
            /*
             if (this.gamepad1.y){
                liftMotor.setPower(-0.4);
                sleep(500);
                liftMotor.setPower(0);
             } else if (this.gamepad1.x){
                liftMotor.setPower(0.4);
                sleep(500);
                liftMotor.setPower(0);

             if (this.gamepad1.dpad_up && !digitalMagLimit.getState() && encoderCount > x){
               liftMotor.setPower(-0.5);
           } else if (this.gamepad1.dpad_down && !digitalMagLimit.getState() && encoderCount < x){
               liftMotor.setPower(0.5);
           } else{
                liftMotor.setPower(0);
           }
           */


           // Manual Lift Controls
           tgtPower3 = this.gamepad2.right_trigger;
           liftMotor.setPower(tgtPower3);
           tgtPower3 = this.gamepad2.left_trigger;
           liftMotor.setPower(-tgtPower3);


           // Arm Control
           tgtPower4 = -this.gamepad2.left_stick_y;
           armMotor.setPower(tgtPower4);


           // Sweeper Control
           tgtPower5 = -this.gamepad2.right_stick_y;
           sweepMotor.setPower(tgtPower5);


           // Sweeper and Arm Controls, Controlled
           if(this.gamepad2.left_bumper){
               sweepMotor.setPower(tgtPower5 / 8);
               armMotor.setPower(tgtPower4 / 2);
           }

          }
       }
   }

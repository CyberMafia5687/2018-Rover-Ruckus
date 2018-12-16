package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Disabled
public class RoverRuckus extends LinearOpMode {
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor armMotor;
    private DcMotor sweepMotor;
    // private Servo handServo;
    Servo handServo = null;


    @Override
    public void runOpMode() {
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        sweepMotor = hardwareMap.get(DcMotor.class, "sweepMotor");
        handServo = hardwareMap.servo.get("handServo");


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
        // double tgtPower6 = 0;


        while (opModeIsActive()){
           tgtPower1 = this.gamepad1.left_stick_y;
           leftMotor.setPower(tgtPower1);

           tgtPower2 = -this.gamepad1.right_stick_y;
           rightMotor.setPower(tgtPower2);

           tgtPower3 = -this.gamepad2.left_stick_y;
           armMotor.setPower(tgtPower3);

           tgtPower4 = -this.gamepad1.right_trigger;
           sweepMotor.setPower(tgtPower4);

           tgtPower5 = this.gamepad1.left_trigger;
           sweepMotor.setPower(tgtPower5);

           if (gamepad2.a){
               handServo.setPosition(0.30);
           } else if(gamepad2.y){
               handServo.setPosition(1);
           } else{
               handServo.setPosition(0.65);
           }


           /*
           if (gamepad2.y){
               handServo.setPosition(0);
           } else if (gamepad2.x || gamepad2.b){
               handServo.setPosition(0.5);
           } else if (gamepad2.a){
               handServo.setPosition(1);
           }
           telemetry.addData("Servo Position", handServo.getPosition());
           */

           telemetry.addData("Status", "Running");
           telemetry.update();
        }
    }
}

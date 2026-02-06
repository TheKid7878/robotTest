/*
Copyright 2025 FIRST Tech Challenge Team FTC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs
 * in either the autonomous or the TeleOp period of an FTC match. The names of OpModes appear on
 * the menu of the FTC Driver Station. When an selection is made from the menu, the corresponding
 * OpMode class is instantiated on the Robot Controller and executed.
 *
 * Remove the @Disabled annotation on the next line or two (if present) to add this OpMode to the
 * Driver Station OpMode list, or add a @Disabled annotation to prevent this OpMode from being
 * added to the Driver Station.
 */
@Autonomous // Driver controlled
// If you wanted to change this to an autonomous op mode replace it with @Autonomous

public class Auto extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor intake;
    private DcMotor index;
    private DcMotor shooter;
    private DcMotorEx back_left;
    private DcMotorEx back_right;
    private DcMotorEx front_left;
    private DcMotorEx front_right;
    private IMU imu;
    private Servo servoTest;
    private boolean isShooting = false;

    static final double COUNTS_PER_REV = 537.7;
    static final double WHEEL_DIAMETER_IN = 4.094;
    static final double WHEEL_CIRCUMFERENCE_IN = WHEEL_DIAMETER_IN * Math.PI;
    static final double COUNTS_PER_INCH = COUNTS_PER_REV / WHEEL_CIRCUMFERENCE_IN;
    // double distanceInches = 24; //1 foot
    //counts = ticks
    //To go from inches to ticks: Multiply by the counts per revolution then divide by wheel circumference

    //RUN_WITHOUT_ENCODER - motor runs purely with the power you give it, no feedback
    //RUN_USING_ENCODER - motor uses its encoder to maintain a consistent velocity
    //RUN_TO_POSITION - motor goes to a target encoder position and stops
    //encoder tells you how far the robot has traveled in "ticks"

    @Override
    public void runOpMode() {
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        intake = hardwareMap.get(DcMotor.class, "intake");
        index = hardwareMap.get(DcMotor.class, "index");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        back_left = hardwareMap.get(DcMotorEx.class, "back_left");
        back_right = hardwareMap.get(DcMotorEx.class, "back_right");
        front_left = hardwareMap.get(DcMotorEx.class, "front_left");
        front_right = hardwareMap.get(DcMotorEx.class, "front_right");
        // imu = hardwareMap.get(IMU.class, "imu");

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //encoder tells you how far the robot has traveled in "ticks"
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //setting encoder to zero, also stops the robot
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        //distanceInches is the physical distance we want the robot to travel
        //COUNTS_PER_INCH is the conversion factor; tells us how many encoder ticks are needed to travel that many inches
        //distanceInches * COUNTS_PER_INCH produces a decimal # (floating-point),
        //so int turns it into a whole # (integer) to make it compatible with the motor API
        // front_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        // front_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        // back_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        // back_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));

        //the sequence (for now):
        //move forward → turn left or right → move forward toward the goal if necessary → shoot three balls
        setTargetPositionForward(54);
        sleep(1000);
        turnLeftForTime(100);
        shoot();
        // sleep(1000);
        // setTargetPositionRight(24);

        //runs until the end of the match (driver presses STOP)
        //isBusy() checks if the motor has reached its target encoder position, or if the error is greater than 0
        while (opModeIsActive() && (front_left.isBusy() || back_left.isBusy() || front_right.isBusy() || back_right.isBusy())) {
            intake.setPower(1); //intake on constantly

            telemetry.addData("Front left motor position", front_left.getCurrentPosition()); 
            telemetry.addData("Back left motor position", back_left.getCurrentPosition()); 
            telemetry.addData("Front right motor position", front_right.getCurrentPosition()); 
            telemetry.addData("Back right motor position", back_right.getCurrentPosition()); 
        
            telemetry.update();
        }
    }

    public void setTargetPositionForward(int distanceInches) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //setting encoder to zero, also stops the robot
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        front_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        front_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        back_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        back_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        
        front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // double TPS = (175/60) * COUNTS_PER_REV;
        double TPS = (100.0 / 60.0) * COUNTS_PER_REV; //ticks per second
        //170 RPM is an estimate of how fast the wheel turns; you can make this smaller but then the robot will move slower
        //170 is a motor speed in RPM (rotations per minute), so we're dividing it by 60 to convert it to RPS (rotations per second)
        //then multiplying by counts per revolution to turn rotations into ticks
        
        front_left.setVelocity(TPS);
        front_right.setVelocity(TPS);
        back_left.setVelocity(TPS);
        back_right.setVelocity(TPS);
    }

    public void setTargetPositionBackward(int distanceInches) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        front_left.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        front_right.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        back_left.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        back_right.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        
        front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // double TPS = (175/60) * COUNTS_PER_REV;
        double TPS = (100.0 / 60.0) * COUNTS_PER_REV; //ticks per second
        //170 RPM is an estimate of how fast the wheel turns; you can make this smaller but then the robot will move slower
        //170 is a motor speed in RPM (rotations per minute), so we're dividing it by 60 to convert it to RPS (rotations per second)
        //then multiplying by counts per revolution to turn rotations into ticks
        
        front_left.setVelocity(TPS);
        front_right.setVelocity(TPS);
        back_left.setVelocity(TPS);
        back_right.setVelocity(TPS);
    }

    public void setTargetPositionRight(int distanceInches) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        front_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        front_right.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        back_left.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        back_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        
        front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // double TPS = (175/60) * COUNTS_PER_REV;
        double TPS = (100.0 / 60.0) * COUNTS_PER_REV; //ticks per second
        //170 RPM is an estimate of how fast the wheel turns; you can make this smaller but then the robot will move slower
        //170 is a motor speed in RPM (rotations per minute), so we're dividing it by 60 to convert it to RPS (rotations per second)
        //then multiplying by counts per revolution to turn rotations into ticks
        
        front_left.setVelocity(TPS);
        front_right.setVelocity(TPS);
        back_left.setVelocity(TPS);
        back_right.setVelocity(TPS);
    }

    public void setTargetPositionLeft(int distanceInches) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        front_left.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        front_right.setTargetPosition((int)(distanceInches * COUNTS_PER_INCH));
        back_left.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        back_right.setTargetPosition((int)(-distanceInches * COUNTS_PER_INCH));
        
        front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // double TPS = (175/60) * COUNTS_PER_REV;
        double TPS = (100.0 / 60.0) * COUNTS_PER_REV; //ticks per second
        //170 RPM is an estimate of how fast the wheel turns; you can make this smaller but then the robot will move slower
        //170 is a motor speed in RPM (rotations per minute), so we're dividing it by 60 to convert it to RPS (rotations per second)
        //then multiplying by counts per revolution to turn rotations into ticks
        
        front_left.setVelocity(TPS);
        front_right.setVelocity(TPS);
        back_left.setVelocity(TPS);
        back_right.setVelocity(TPS);
    }

    public void turnRightForTime(double time) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        front_left.setPower(-1); //using setPower because setVelocity uses encoders
        back_left.setPower(-1);
        front_right.setPower(-1);
        back_right.setPower(-1);

        sleep(time);

        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    public void turnLeftForTime(double time) {
        if (isShooting) return;

        front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        front_left.setPower(1);
        back_left.setPower(1);
        front_right.setPower(1);
        back_right.setPower(1);

        sleep(time);

        front_left.setPower(0);
        front_right.setPower(0);
        back_left.setPower(0);
        back_right.setPower(0);
    }

    function shoot() {
        isShooting = true; //don't move while shooting
        index.setPower(-1);
        shooter.setPower(1);
    }
}

 //we don't need stopMotors() because we're using RUN_TO_POSITION which automatically stops the motors once the robot reaches its destination
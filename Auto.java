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
    private DcMotor back_left;
    private DcMotor back_right;
    private DcMotor front_left;
    private DcMotor front_right;
    private IMU imu;
    private Servo servoTest;

    //RUN_WITHOUT_ENCODER - motor runs purely with the power you give it, no feedback
    //RUN_USING_ENCODER - motor uses its encoder to maintain a consistent velocity
    //RUN_TO_POSITION - motor goes to a target encoder position and stops

    @Override
    public void runOpMode() {
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        intake = hardwareMap.get(DcMotor.class, "intake");
        index = hardwareMap.get(DcMotor.class, "index");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        back_right = hardwareMap.get(DcMotor.class, "back_right");
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
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
        front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized"); 
        telemetry.update();
        waitForStart();

        //runs until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            driveMotorsForward(200); //will turn the motors at 200 ticks per second
            sleep(5000);
            stopMotors();
            break;
        }
    }

    public void driveMotorsForward(float velocity) {
        double frontLeftVelocity = velocity * -1;
        double backLeftVelocity = velocity * -1;
        double frontRightVelocity = velocity;
        double backRightVelocity = velocity;
        
        front_left.setVelocity(frontLeftVelocity); //using setVelocity() to take full advantage of RUN_USING_ENCODER
        back_left.setVelocity(backLeftVelocity);
        front_right.setVelocity(frontRightVelocity);
        back_right.setVelocity(backRightVelocity);
    }

    public void stopMotors() {
        double frontLeftVelocity = 0;
        double backLeftVelocity = 0;
        double frontRightVelocity = 0;
        double backRightVelocity = 0;
        
        front_left.setVelocity(frontLeftVelocity);
        back_left.setVelocity(backLeftVelocity);
        front_right.setVelocity(frontRightVelocity);
        back_right.setVelocity(backRightVelocity);
    }
}

//------------------------this is not a part of the actual code----------------

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
@TeleOp // Driver controlled
// If you wanted to change this to an autonomous op mode replace it with @Autonomous

public class driver extends LinearOpMode {
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

        front_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        front_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        back_left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        back_right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addData("Status", "Initialized"); 
        telemetry.update();
        waitForStart();

        //runs until the end of the match (driver presses STOP)
        double tgtPower = 0;
        while (opModeIsActive()) {
            //shooting mechanism
            //100% = 6000 rpm
            float indexSpeed = Math.abs(gamepad1.right_trigger);
            float shooterSpeed = Math.abs(gamepad1.left_trigger);
            index.setPower(-indexSpeed);
            shooter.setPower(shooterSpeed);
            // telemetry.addData("left trigger", gamepad1.left_trigger);
            // telemetry.addData("right trigger", gamepad1.right_trigger);
            
            //intake
            if (gamepad1.right_bumper) {
                tgtPower = 1;
                // telemetry.addData("Status", "Intake running");
            } else if (gamepad1.left_bumper) {
                tgtPower = 0;
            }
            intake.setPower(tgtPower);
            
            //directions
            // telemetry.addData("x: ", -gamepad1.left_stick_x);
            // telemetry.addData("y: ", -gamepad1.left_stick_y);
            float gamepadX = -gamepad1.left_stick_x;
            float gamepadY = -gamepad1.left_stick_y;
            // isJoystickForward(gamepadX, gamepadY){return boolepressio)
            if (gamepadX == 0 && gamepadY > 0) { //0.1 & -0.1 instead of 0
                telemetry.addData("Robot", "forward");
                float speed = Math.abs(gamepadY);
                driveMotorsForward(speed);
            } else if (gamepadX == 0 && gamepadY < 0) {
                telemetry.addData("Robot", "backward");
                float speed = Math.abs(gamepadY);
                driveMotorsBackward(speed);
            } else if (gamepadX < 0 && gamepadY == 0) {
                telemetry.addData("Robot", "left");
                float speed = Math.abs(gamepadX);
                driveMotorsLeft(speed);
            } else if (gamepadX > 0 && gamepadY == 0) {
                telemetry.addData("Robot", "right");
                float speed = Math.abs(gamepadX);
                driveMotorsRight(speed);
            }else if (gamepadX > 0 && gamepadY > 0) {
                telemetry.addData("Robot", "right diagonal forward");
                float speed = (float)Math.sqrt(gamepadX * gamepadX + gamepadY * gamepadY);
                driveMotorsRightDiagonalF(speed);
            } else if (gamepadX < 0 && gamepadY > 0) {
                telemetry.addData("Robot", "left diagonal forward");
                float speed = (float)Math.sqrt(gamepadX * gamepadX + gamepadY * gamepadY);
                //↑ the square root of x squared plus y squared = magnitude, how much the stick is pushed overall
                driveMotorsLeftDiagonalF(speed);
            } else if (gamepadX > 0 && gamepadY < 0) {
                telemetry.addData("Robot", "right diagonal backward");
                float speed = (float)Math.sqrt(gamepadX * gamepadX + gamepadY * gamepadY);
                driveMotorsRightDiagonalB(speed);
            } else if (gamepadX < 0 && gamepadY < 0) {
                telemetry.addData("Robot", "left diagonal backward");
                float speed = (float)Math.sqrt(gamepadX * gamepadX + gamepadY * gamepadY);
                driveMotorsLeftDiagonalF(speed);
            } else {
                stopMotors();
            }
            
            //directions - adjustments
            if (gamepad1.dpad_up) {
                driveMotorsForward(1);
            } else if (gamepad1.dpad_down) {
                driveMotorsBackward(1);
            } else if (gamepad1.dpad_right) {
                driveMotorsRight(1);
            } else if (gamepad1.dpad_left) {
                driveMotorsLeft(1);
            } else {
                stopMotors();
            }
            
            //turning
            // telemetry.addData("Right stick: ", gamepad1.right_stick_x);
            float gamepadX2 = gamepad1.right_stick_x;
            if (gamepad1.right_stick_x > 0) {
                // telemetry.addData("Robot: ", "turn right");
                float speed = Math.abs(gamepadX2);
                turnRight(speed);
                
            } else if (gamepad1.right_stick_x < 0) {
                // telemetry.addData("Robot: ", "turn left");
                float speed = Math.abs(gamepadX2);
                turnLeft(speed);
            }
            
            telemetry.update();
        }
    }
        
    //clockwise = negative
    //counterclockwise = positive
    
    public void driveMotorsForward(float speed) {
        double frontLeftPower = speed * -1;
        double backLeftPower = speed * -1;
        double frontRightPower = speed;
        double backRightPower = speed;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
        
        // front_left = "move clockwise by speed";
        // back_left = "move clockwise by speed";
        // front_right = "move counterclockwise by speed";
        // back_right = "move counterclockwise by speed";
    }

    public void driveMotorsBackward(float speed) {
        double frontLeftPower = speed;
        double backLeftPower = speed;
        double frontRightPower = speed * -1;
        double backRightPower = speed * -1;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
        
        // front_left = "move counterclockwise by speed";
        // back_left = "move counterclockwise by speed";
        // front_right = "move clockwise by speed";
        // back_right = "move clockwise by speed";
    }

    public void driveMotorsRight(float speed) {
        double frontLeftPower = speed * -1;
        double backLeftPower = speed;
        double frontRightPower = speed * -1;
        double backRightPower = speed;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "move clockwise by speed";
        // back_left = "move counterclockwise by speed";
        // front_right = "move clockwise by speed";
        // back_right = "move counterclockwise by speed";
    }

    public void driveMotorsLeft(float speed) {
        double frontLeftPower = speed;
        double backLeftPower = speed * -1;
        double frontRightPower = speed;
        double backRightPower = speed * -1;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "move counterclockwise by speed";
        // back_left = "move clockwise by speed";
        // front_right = "move counterclockwise by speed";
        // back_right = "move clockwise by speed";
    }

    public void driveMotorsLeftDiagonalF(float speed) {
        double frontLeftPower = 0;
        double backLeftPower = speed * -1;
        double frontRightPower = speed;
        double backRightPower = 0;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "set to zero";
        // back_left = "move clockwise by speed";
        // front_right = "move counterclockwise by speed";
        // back_right = "set to zero";
    }

    public void driveMotorsLeftDiagonalB(float speed) {
        double frontLeftPower = speed;
        double backLeftPower = 0;
        double frontRightPower = 0;
        double backRightPower = speed * -1;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "move counterclockwise by speed";
        // back_left = "set to zero";
        // front_right = "set to zero";
        // back_right = "move clockwise by speed";
    }

    public void driveMotorsRightDiagonalF(float speed) {
        double frontLeftPower = speed * -1;
        double backLeftPower = 0;
        double frontRightPower = 0;
        double backRightPower = speed;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "move clockwise by speed";
        // back_left = "set to zero";
        // front_right = "set to zero";
        // back_right = "move counterclockwise by speed";
    }

    public void driveMotorsRightDiagonalB(float speed) {
        double frontLeftPower = 0;
        double backLeftPower = speed;
        double frontRightPower = speed * -1;
        double backRightPower = 0;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);

        // front_left = "set to zero";
        // back_left = "move counterclockwise by speed";
        // front_right = "move clockwise by speed";
        // back_right = "set to zero";
    }
    
    public void stopMotors() {
        double frontLeftPower = 0;
        double backLeftPower = 0;
        double frontRightPower = 0;
        double backRightPower = 0;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
    }
    
    public void turnRight(float speed) {
        double frontLeftPower = speed * -1;
        double backLeftPower = speed * -1;
        double frontRightPower = speed * -1;
        double backRightPower = speed * -1;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
    }
    
    public void turnLeft(float speed) {
        double frontLeftPower = speed;
        double backLeftPower = speed;
        double frontRightPower = speed;
        double backRightPower = speed;
        
        front_left.setPower(frontLeftPower);
        back_left.setPower(backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
    }
}
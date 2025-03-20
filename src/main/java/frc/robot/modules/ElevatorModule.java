package frc.robot.modules;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;

public class ElevatorModule {

    public enum ModuleStates {
        UNKNOWN,
        MANUAL,
        HOMING,
        HOME,
        HUMAN_PICKUP,
        L1,
        L2,
        L3,
        L4,
        MOVING;
    }

    public enum RequestStates {
        FIND_HOME,
        HOME,
        MANUAL,
        HUNMAN_PICKUP,
        L1,
        L2,
        L3,
        L4;
    }

    public ModuleStates currentState;
    public ModuleStates nextState;
    public RequestStates requestedState = RequestStates.FIND_HOME;

    public final static ModuleStates initialState = ModuleStates.UNKNOWN;

    public SparkMax rightMotor;
    public RelativeEncoder rightEncoder;
    public SparkMaxConfig rightConfig;

    public SparkMax leftMotor;
    public RelativeEncoder leftEncoder;
    public SparkMaxConfig leftConfig;

    public Joystick controller;

    private Timer homing_timer;

    double elevator_feedforward = 0.02;

    /* Elevator PID */
    public boolean elevator_found;

    private double L1_HEIGHT = 10;
    private double L2_HEIGHT = 23;
    private double L3_HEIGHT = 53;
    private double L4_HEIGHT = 99;

    public PIDController pid_controller;
    public double target_position;

    public ElevatorModule(int rightMotorID, int leftMotorID, Joystick drive_Controller) {
        this.currentState = initialState;
        this.nextState = ModuleStates.UNKNOWN;


        this.rightMotor = new SparkMax(rightMotorID, MotorType.kBrushless);
        this.rightEncoder = this.rightMotor.getEncoder();
        this.rightConfig = new SparkMaxConfig();
        this.rightConfig.idleMode(IdleMode.kBrake);
        this.rightConfig.openLoopRampRate(0.5);
        this.rightMotor.configure(this.rightConfig, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

        this.leftMotor = new SparkMax(leftMotorID, MotorType.kBrushless);
        this.leftEncoder = this.leftMotor.getEncoder();
        this.leftConfig = new SparkMaxConfig();
        this.leftConfig.follow(rightMotorID, true);
        this.leftConfig.idleMode(IdleMode.kBrake);
        this.leftConfig.openLoopRampRate(0.5);
        this.leftMotor.configure(this.leftConfig, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

        this.controller = drive_Controller;

        this.homing_timer = new Timer();

        this.elevator_found = false;
        this.pid_controller = new PIDController(0.04, 0, 0.003);
        target_position = 0;

    }

    public void request_state(RequestStates state) {
        this.requestedState = state;

        switch(state) {
            case FIND_HOME:
                currentState = ModuleStates.HOMING;
                homing_timer.restart();

                break;

            case HOME:
                nextState = ModuleStates.HOME;
                currentState = ModuleStates.MOVING;
                target_position = 0;
                break;

            case MANUAL:
                currentState = ModuleStates.MANUAL;

                break;

            case HUNMAN_PICKUP:

                break;

            case L1:
                nextState = ModuleStates.L1;
                currentState = ModuleStates.MOVING;
                target_position = L1_HEIGHT;
                break;

            case L2:
                nextState = ModuleStates.L2;
                currentState = ModuleStates.MOVING;
                target_position = L2_HEIGHT;
                break;

            case L3:
                nextState = ModuleStates.L3;
                currentState = ModuleStates.MOVING;
                target_position = L3_HEIGHT;
                break;

            case L4:
                nextState = ModuleStates.L4;
                currentState = ModuleStates.MOVING;
                target_position = L4_HEIGHT;
                break;

        }

    }

    public void update() {

        switch (currentState) {
            case UNKNOWN:

                break;

            case MANUAL:
                double power = MathUtil.applyDeadband(-this.controller.getY(), 0.15, 1);
                power = MathUtil.clamp(power, -0.4, .4);
                rightMotor.set(power + elevator_feedforward);

                break;

            case HOMING:
                // Let elevator fall slowly
                rightMotor.set(0.005);

                if ((rightEncoder.getVelocity() < 5 ) && homing_timer.hasElapsed(0.5))
                {
                    elevator_found = true;
                    homing_timer.stop();
                    rightMotor.set(elevator_feedforward);

                    rightEncoder.setPosition(0);
                    leftEncoder.setPosition(0);
                    currentState = ModuleStates.MANUAL;
                }

                break;

            case HOME:

                break;

            case HUMAN_PICKUP:

                break;

            case L1:

                break;

            case L2:

                break;

            case L3:

                break;

            case L4:

                break;

            case MOVING:
                if (elevator_found)
                {
                    double pid_term = pid_controller.calculate(rightEncoder.getPosition(), target_position);

                    double auto_power = MathUtil.clamp(pid_term + elevator_feedforward, -0.9, 0.9);

                    rightMotor.set(auto_power);

                    if (pid_controller.atSetpoint())
                    {
                        currentState = nextState;
                        rightMotor.set(elevator_feedforward);
                    }
                }

                break;

        }

    }

}

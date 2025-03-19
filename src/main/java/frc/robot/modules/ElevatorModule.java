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
import edu.wpi.first.wpilibj.Encoder;
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
    public RequestStates requestedState = RequestStates.FIND_HOME;

    public final static ModuleStates initialState = ModuleStates.UNKNOWN;

    public SparkMax rightMotor;
    public RelativeEncoder rightEncoder;
    public SparkMaxConfig rightConfig;

    public SparkMax leftMotor;
    public RelativeEncoder leftEncoder;
    public SparkMaxConfig leftConfig;

    public XboxController controller;

    private Timer homing_timer;

    double elevator_feedforward = 0.03;

    public ElevatorModule(int rightMotorID, int leftMotorID, XboxController drive_Controller) {
        this.currentState = initialState;

        this.rightMotor = new SparkMax(rightMotorID, MotorType.kBrushless);
        this.rightEncoder = this.rightMotor.getEncoder();
        this.rightConfig = new SparkMaxConfig();
        this.rightMotor.configure(this.rightConfig, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        this.rightConfig.idleMode(IdleMode.kBrake);

        this.leftMotor = new SparkMax(leftMotorID, MotorType.kBrushless);
        this.leftEncoder = this.leftMotor.getEncoder();
        this.leftConfig = new SparkMaxConfig();
        this.leftMotor.configure(this.leftConfig, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        this.leftConfig.follow(rightMotorID, true);
        this.leftConfig.idleMode(IdleMode.kBrake);

        this.controller = drive_Controller;

        this.homing_timer = new Timer();

    }

    public void request_state(RequestStates state) {
        this.requestedState = state;

        switch(state) {
            case FIND_HOME:
                currentState = ModuleStates.HOMING;
                homing_timer.restart();

                break;

            case HOME:

                break;

            case MANUAL:

                break;

            case HUNMAN_PICKUP:

                break;

            case L1:

                break;

            case L2:

                break;

            case L3:

                break;

            case L4:

                break;

        }

    }

    public void update() {

        switch (currentState) {
            case UNKNOWN:

                break;

            case MANUAL:
                double power = MathUtil.applyDeadband(this.controller.getLeftX(), 0.15, 1);
                power = MathUtil.clamp(power, -0.2, .2) / 2;
                rightMotor.set(power + elevator_feedforward);

                break;

            case HOMING:
                // Let elevator fall slowly
                rightMotor.set(0.005);

                if ((rightEncoder.getVelocity() < 5 ) && homing_timer.hasElapsed(0.5))
                {
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

                break;

        }

    }

}

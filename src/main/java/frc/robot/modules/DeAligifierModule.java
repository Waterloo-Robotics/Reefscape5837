package frc.robot.modules;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class DeAligifierModule {
    public enum ModuleStates {
        UNKNOWN,
        MOVING,
        FIND_HOME,
        HOME,
        MANUAL,
        IN,
        LOW,
        HIGH;
    }

    public enum RequestStates {
        FIND_HOME,
        HOME,
        MANUAL,
        IN,
        LOW,
        HIGH;

    }

    public ModuleStates currentState;
    public ModuleStates nextState;
    public RequestStates requestedState = RequestStates.FIND_HOME;

    public final static ModuleStates initialState = ModuleStates.UNKNOWN;

    public SparkMax DeAligifierMotor;
    public SparkMaxConfig DeAligifierConfig;
    public RelativeEncoder DeAligifierEncoder;

    private double IN = 20;
    private double LOW = 30;
    private double HIGH = 45;

    public PIDController pid_controller;
    public double target_position;
    public Joystick controller;

    public boolean DeAligifier_found;

    private Timer homing_timer;

    public DeAligifierModule(int DeAligifierMotorID, Joystick drive_Controller) {
        this.currentState = initialState;
        this.DeAligifierMotor = new SparkMax(DeAligifierMotorID, MotorType.kBrushless);

        this.DeAligifierConfig = new SparkMaxConfig();
        this.DeAligifierConfig.smartCurrentLimit(20);
        this.DeAligifierConfig.inverted(false);

        this.DeAligifierMotor.configure(this.DeAligifierConfig, ResetMode.kNoResetSafeParameters,
                PersistMode.kPersistParameters);

        this.DeAligifierEncoder = this.DeAligifierMotor.getEncoder();

        this.homing_timer = new Timer();

        this.controller = drive_Controller;
        this.pid_controller = new PIDController(0.02, 0, 0);
        this.pid_controller.setTolerance(1);
    }

    public void request_state(RequestStates state) {
        this.requestedState = state;

        switch (state) {
            case FIND_HOME:
                currentState = ModuleStates.FIND_HOME;
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

            case IN:
                nextState = ModuleStates.IN;
                currentState = ModuleStates.MOVING;
                target_position = IN;
                break;

            case LOW:
                nextState = ModuleStates.LOW;
                currentState = ModuleStates.MOVING;
                target_position = LOW;
                break;

            case HIGH:
                nextState = ModuleStates.HIGH;
                currentState = ModuleStates.MOVING;
                target_position = HIGH;
                break;

        }

    }

    public void update() {
        switch (currentState) {

            case FIND_HOME:
                // Drive the motor down
                DeAligifierMotor.set(-0.1);

                // Wait for it to hit the bottom
                if ((DeAligifierMotor.getOutputCurrent() > 2) && homing_timer.hasElapsed(0.1))
                {
                    DeAligifier_found = true;
                    homing_timer.stop();
                    DeAligifierMotor.set(0);

                    DeAligifierEncoder.setPosition(0);
                    this.request_state(RequestStates.HOME);
                }

                break;

            case UNKNOWN:

                break;

            case MANUAL:
                double power = MathUtil.applyDeadband(this.controller.getY(), 0.15, 1);
                power = MathUtil.clamp(power, -0.3, 0.3);
                DeAligifierMotor.set(power);

                break;

            case MOVING:
            case HOME:
            case IN:
            case LOW:
            case HIGH:

                if (DeAligifier_found) {
                    double pid_term = pid_controller.calculate(DeAligifierEncoder.getPosition(), target_position);

                    double auto_power = MathUtil.clamp(pid_term, -0.9, 0.9);

                    DeAligifierMotor.set(auto_power);

                    if (pid_controller.atSetpoint()) {
                        currentState = nextState;
                        DeAligifierMotor.set(0);
                    }
                }

                break;

        }

    }

}

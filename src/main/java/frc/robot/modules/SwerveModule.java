package frc.robot.modules;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {
    /* Motors */
    public SparkMax drive_spark;
    public SparkMax steer_spark;

    /* Motor Configurations */
    public SparkBaseConfig drive_config;
    public SparkBaseConfig steer_config;

    /* Encoders */
    public CANcoder steer_cancoder;
    public RelativeEncoder drive_encoder;

    /* PID Controllers */
    public PIDController drive_controller;
    public PIDController angle_controller;

    private Rotation2d last_angle;

    public SwerveModuleState last_state;

    public SwerveModule(int steer_id, int drive_id, int angle_id, boolean reverse_drive) {
        drive_spark = new SparkMax(drive_id, MotorType.kBrushless);
        drive_config.idleMode(IdleMode.kBrake);
        drive_config.inverted(reverse_drive);
        /* By limiting the ramp rate to 0.5 seconds the peak current goes down from 120A to 80A */
        drive_config.openLoopRampRate(0.5);

        drive_encoder = drive_spark.getEncoder();

        double kWheelDiameter = Units.inchesToMeters(4.0);
        double kWheelCircumference = kWheelDiameter * Math.PI;
        double kDriveRatio = 6.12 / 1.0;
        double kDistancePerMotorRotation = kWheelCircumference / kDriveRatio;

        drive_config.encoder.velocityConversionFactor(kDistancePerMotorRotation);
        drive_config.encoder.positionConversionFactor(kDistancePerMotorRotation);
        drive_spark.configure(drive_config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

        steer_spark = new SparkMax(steer_id, MotorType.kBrushless);
        steer_config.inverted(true);
        steer_cancoder = new CANcoder(angle_id);

        drive_controller = new PIDController(0.02, 0, 0);

        angle_controller = new PIDController(0.004, 0, 0);
        angle_controller.setTolerance(1);
        angle_controller.enableContinuousInput(-180, 180);

        last_angle = Rotation2d.fromDegrees(get_raw_angle());
    }

    public void set_module_state(SwerveModuleState state) {
        /*
         * Optimize the state - this handles reversing direction and minimizes the
         * change in heading
         */
        //depreciated adjusted to below. TEST
        //state =  SwerveModuleState.optimize(state, last_angle);
        state.optimize(last_angle);
        
        
        // double drive_output = MathUtil.clamp(drive_controller.calculate(drive_encoder.getVelocity(), state.speedMetersPerSecond), -0.05, 0.05);
        double drive_output = MathUtil.clamp(state.speedMetersPerSecond/Units.feetToMeters(16), -0.85, 0.85);
        double steer_output = MathUtil.clamp(angle_controller.calculate(get_raw_angle(), state.angle.getDegrees()), -0.5, 0.5);

        /* Set the new powers to the SPARK Max controllers */
        drive_spark.set(drive_output);
        steer_spark.set(steer_output);

        /* Update last angle for use next time */
        last_angle = state.angle;
        last_state = state;

    }

    public double get_raw_angle() {
        return steer_cancoder.getAbsolutePosition().getValueAsDouble() * 360 - 180;
    }

    public SwerveModulePosition get_module_position() {
        return new SwerveModulePosition(drive_encoder.getPosition(), Rotation2d.fromDegrees(this.get_raw_angle()));
    }

}

package frc.robot.modules;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.Encoder;

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


    public ElevatorModule(int rightMotorID, int leftMotorID) {
                this.rightMotor = new SparkMax(rightMotorID, MotorType.kBrushless);
                this.leftMotor = new SparkMax(leftMotorID, MotorType.kBrushless);
                this.rightEncoder = this.rightMotor.getEncoder();
                this.leftEncoder = this.leftMotor.getEncoder();
                p
    }

    public void request_state(RequestStates state) {
        this.requestedState = state;


    }

    public void update() {
        
    }
    
}

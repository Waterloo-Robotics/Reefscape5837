package frc.robot.modules;

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
    public Encoder rightEncoder;

    public SparkMax leftMotor;
    public Encoder leftEncoder;

    public ElevatorModule(int outtakeMotorID, int backBeamID, int frontBeamID) {

    }

    public void request_state(RequestStates state) {
        this.requestedState = state;


    }

    public void update() {
        
    }
    
}

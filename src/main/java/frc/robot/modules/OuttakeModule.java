package frc.robot.modules;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;

public class OuttakeModule {

    public enum ModuleStates {
        EMPTY,
        IDENTIFY_CORAL,
        WAITING_FOR_CORAL,
        POSITIONING_CORAL,
        CORAL_IN_POSITION,
        ALGE_POSITION,
        SCORING;
    }

    public enum RequestStates {
        STOP,
        IDENTIFY_CORAL,
        POSITION_CORAL,
        POSITION_CORAL_FOR_ALGE,
        SCORE_CORAL,
        EMPTY_OUTTAKE;
    }

    public ModuleStates currentState;
    public RequestStates requestedState = RequestStates.STOP;
    
    public final static ModuleStates initialState = ModuleStates.EMPTY;

    public SparkMax outtakeMotor;
    public Encoder outtakeEncoder;

    public DigitalInput backBeam;
    public DigitalInput frontBeam;


    public OuttakeModule(int outtakeMotorID, int backBeamID, int frontBeamID) {

    }

    public void request_state(RequestStates state) {
        this.requestedState = state;


    }

    public void update() {
        
    }
    
}

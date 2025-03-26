package frc.robot.modules;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

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
        INTAKE,
        POSITION_CORAL_FOR_ALGE,
        SCORE_CORAL,
        EMPTY_OUTTAKE;
    }

    public ModuleStates currentState;
    public RequestStates requestedState = RequestStates.STOP;

    public final static ModuleStates initialState = ModuleStates.EMPTY;

    public SparkMax outtakeMotor;
    public SparkMaxConfig outtakeConfig;
    public RelativeEncoder outtakeEncoder;

    public DigitalInput backBeam;
    public DigitalInput frontBeam;

    public OuttakeModule(int outtakeMotorID, int backBeamID, int frontBeamID) {

        this.currentState = initialState;
        this.outtakeMotor = new SparkMax(outtakeMotorID, MotorType.kBrushless);

        this.outtakeConfig = new SparkMaxConfig();
        this.outtakeConfig.smartCurrentLimit(24);
        this.outtakeMotor.configure(this.outtakeConfig, ResetMode.kNoResetSafeParameters,
                PersistMode.kPersistParameters);

        this.outtakeEncoder = this.outtakeMotor.getEncoder();

        this.backBeam = new DigitalInput(backBeamID);
        this.frontBeam = new DigitalInput(frontBeamID);

    }

    public void request_state(RequestStates state) {
        this.requestedState = state;
        switch (state) {
            case STOP:
                this.currentState = ModuleStates.EMPTY;

                break;

            case IDENTIFY_CORAL:

                break;

            case INTAKE:
                if (this.frontBeam.get()) {
                    this.currentState = ModuleStates.WAITING_FOR_CORAL;
                    this.outtakeMotor.set(.75);
                }

                break;

            case POSITION_CORAL_FOR_ALGE:

                break;

            case SCORE_CORAL:

                if (!this.frontBeam.get()) {
                    this.currentState = ModuleStates.SCORING;
                    this.outtakeMotor.set(.5);

                }

                break;

            case EMPTY_OUTTAKE:

                break;

        }

    }

    public void update() {
        switch (currentState) {
            case EMPTY:
                this.outtakeMotor.set(0);

                break;

            case IDENTIFY_CORAL:

                break;

            case WAITING_FOR_CORAL:
                if (!this.backBeam.get()) {
                    this.outtakeMotor.set(0.25);
                    this.currentState = ModuleStates.POSITIONING_CORAL;
                }

                break;

            case POSITIONING_CORAL:
                if (this.backBeam.get()) {
                    this.outtakeMotor.set(0);
                    this.currentState = ModuleStates.CORAL_IN_POSITION;

                }

                break;

            case CORAL_IN_POSITION:

                break;

            case ALGE_POSITION:

                break;

            case SCORING:
                if (this.frontBeam.get()) {
                    this.currentState = ModuleStates.EMPTY;

                }

                break;

        }

    }

}

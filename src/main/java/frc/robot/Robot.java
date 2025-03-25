// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.modules.DeAligifierModule;
import frc.robot.modules.ElevatorModule;
import frc.robot.modules.OuttakeModule;
import frc.robot.modules.SwerveBaseModule;
import frc.robot.modules.SwerveBaseModule.DriveBaseStates;

public class Robot extends TimedRobot {

  XboxController driver_controller = new XboxController(1);
  Joystick farmSim1 = new Joystick(2);
  Joystick farmSim2 = new Joystick(3);
  SwerveBaseModule drivebase = new SwerveBaseModule(driver_controller);

  OuttakeModule outtake = new OuttakeModule(22, 7, 6);
  ElevatorModule elevator = new ElevatorModule(20, 21, farmSim1);
  DeAligifierModule DeAligifier = new DeAligifierModule(0, farmSim2);
  int autoStep = 1;
  Timer autoTimer = new Timer();

  public Robot() {
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putBoolean("backBeam", outtake.backBeam.get());
    SmartDashboard.putBoolean("frontBeam", outtake.frontBeam.get());
    SmartDashboard.putNumber("elevator Power", elevator.rightMotor.get());
    SmartDashboard.putNumber("elevator encdor", elevator.rightEncoder.getPosition());
    SmartDashboard.putNumber("R Elevator Current", elevator.rightMotor.getOutputCurrent());
    SmartDashboard.putNumber("L Elevator Current", elevator.leftMotor.getOutputCurrent());
    SmartDashboard.putNumber("Outtake", outtake.outtakeMotor.getOutputCurrent());

  }

  @Override
  public void autonomousInit() {
    elevator.elevator_found = true;
    autoTimer.reset();
    drivebase.gyro.reset();
    drivebase.current_state = DriveBaseStates.STOP;
    elevator.currentState = ElevatorModule.ModuleStates.MANUAL;
    autoStep = 1;

  }

  @Override
  public void autonomousPeriodic() {
    if (autoStep == 1) {
      drivebase.current_state = DriveBaseStates.STRAIGHT;
      autoTimer.start();
      autoStep = 2;
    }

    if (autoStep == 2) {
      if (autoTimer.hasElapsed(2)) {
        drivebase.current_state = DriveBaseStates.STOP;
        autoTimer.stop();
        autoStep = 3;
      }
    }

    if (autoStep == 3) {
      elevator.request_state(ElevatorModule.RequestStates.L4);
      autoStep = 4;
    }

    if (autoStep == 4) {
      if (elevator.currentState == ElevatorModule.ModuleStates.L4) {
        outtake.request_state(OuttakeModule.RequestStates.SCORE_CORAL);
        autoStep = 5;
      }
    }

    if (autoStep == 5) {
      if (outtake.currentState == OuttakeModule.ModuleStates.EMPTY) {
        elevator.request_state(ElevatorModule.RequestStates.HOME);
        autoStep = 6;
      }
    }

    drivebase.update();
    elevator.update();
    outtake.update();
  }

  @Override
  public void teleopInit() {
    drivebase.gyro.reset();

    drivebase.current_state = DriveBaseStates.XBOX;
    elevator.currentState = ElevatorModule.ModuleStates.MANUAL;
  }

  @Override
  public void teleopPeriodic() {
    // Elevator
    if (farmSim2.getRawButtonPressed(5) && outtake.backBeam.get()) {
      elevator.request_state(ElevatorModule.RequestStates.HOME);
      drivebase.set_max_drive_speed(1);
    }

    if (farmSim1.getRawButtonPressed(12) && outtake.backBeam.get()) {
      elevator.request_state(ElevatorModule.RequestStates.L1);
      drivebase.set_max_drive_speed(1);
    }

    if (farmSim1.getRawButtonPressed(11) && outtake.backBeam.get()) {
      elevator.request_state(ElevatorModule.RequestStates.L2);
      drivebase.set_max_drive_speed(0.60);
    }

    if (farmSim1.getRawButtonPressed(6) && outtake.backBeam.get()) {
      elevator.request_state(ElevatorModule.RequestStates.L3);
      drivebase.set_max_drive_speed(0.4);
    }

    if (farmSim1.getRawButtonPressed(1) && outtake.backBeam.get()) {
      elevator.request_state(ElevatorModule.RequestStates.L4);
      drivebase.set_max_drive_speed(0.1);
    }

    if (farmSim1.getRawButtonPressed(5)) {
      elevator.request_state(ElevatorModule.RequestStates.FIND_HOME);
    }

    if (farmSim2.getRawButtonPressed(7)) {
      elevator.request_state(ElevatorModule.RequestStates.MANUAL);
    }

    // Outtake
    if (farmSim1.getRawButtonPressed(14)) {
      outtake.request_state(OuttakeModule.RequestStates.INTAKE);
    }

    if (farmSim1.getRawButtonPressed(16)) {
      outtake.request_state(OuttakeModule.RequestStates.STOP);
    }

    if (driver_controller.getRightTriggerAxis() > 0.25) {
      outtake.request_state(OuttakeModule.RequestStates.SCORE_CORAL);
    }

    // DeAligifier

    if (farmSim2.getRawButtonPressed(7)) {
      DeAligifier.request_state(DeAligifierModule.RequestStates.MANUAL);

      if (elevator.currentState == ElevatorModule.ModuleStates.MANUAL) {
          elevator.currentState = ElevatorModule.ModuleStates.UNKNOWN;
      }
    }

    drivebase.update();
    outtake.update();
    elevator.update();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {

    drivebase.gyro.reset();
    drivebase.current_state = DriveBaseStates.XBOX;
    elevator.currentState = ElevatorModule.ModuleStates.MANUAL;

  }

  @Override
  public void testPeriodic() {
    if (driver_controller.getAButtonPressed()) {
      outtake.request_state(OuttakeModule.RequestStates.INTAKE);
    }

    if (driver_controller.getBButtonPressed()) {
      outtake.request_state(OuttakeModule.RequestStates.STOP);
    }

    if (driver_controller.getYButtonPressed()) {
      outtake.request_state(OuttakeModule.RequestStates.SCORE_CORAL);
    }

    if (driver_controller.getStartButtonPressed()) {
      elevator.request_state(ElevatorModule.RequestStates.FIND_HOME);
    }

    if (driver_controller.getPOV() == 180) {
      elevator.request_state(ElevatorModule.RequestStates.L1);
    }

    if (driver_controller.getPOV() == 90) {
      elevator.request_state(ElevatorModule.RequestStates.L2);
    }

    if (driver_controller.getPOV() == 0) {
      elevator.request_state(ElevatorModule.RequestStates.L3);
    }

    if (driver_controller.getPOV() == 270) {
      elevator.request_state(ElevatorModule.RequestStates.L4);
    }

    if (driver_controller.getLeftBumperButtonPressed()) {
      elevator.request_state(ElevatorModule.RequestStates.HOME);
    }

    if (driver_controller.getRightBumperButtonPressed()) {
      elevator.request_state(ElevatorModule.RequestStates.MANUAL);
    }

    if (driver_controller.getXButtonPressed()) {
      elevator.rightEncoder.setPosition(0);
      elevator.leftEncoder.setPosition(0);
    }
    // drivebase.update();
    outtake.update();
    elevator.update();
  }

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}

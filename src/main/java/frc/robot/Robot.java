// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.modules.ElevatorModule;
import frc.robot.modules.OuttakeModule;
import frc.robot.modules.SwerveBaseModule;
import frc.robot.modules.SwerveBaseModule.DriveBaseStates;

public class Robot extends TimedRobot {

  XboxController driver_controller = new XboxController(2);
  // Joystick farmSim1 = new Joystick(4);
  // Joystick farmSim2 = new Joystick(5);
  SwerveBaseModule drivebase = new SwerveBaseModule(driver_controller);

  OuttakeModule outtake = new OuttakeModule(22, 7, 6);
  ElevatorModule elevator = new ElevatorModule(20, 21, driver_controller);

  public Robot()
  {
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putBoolean("backBeam", outtake.backBeam.get());
    SmartDashboard.putBoolean("frontBeam", outtake.frontBeam.get());
    SmartDashboard.putNumber("elevator Power", elevator.rightMotor.get());
    SmartDashboard.putNumber("elevator encdor", elevator.rightEncoder.getPosition());

  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
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

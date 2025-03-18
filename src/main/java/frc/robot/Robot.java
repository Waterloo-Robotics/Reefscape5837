// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.modules.SwerveBaseModule;
import frc.robot.modules.SwerveBaseModule.DriveBaseStates;


public class Robot extends TimedRobot {

  XboxController driver_controller = new XboxController(2);
  Joystick farmSim1 = new Joystick(4);
  Joystick farmSim2 = new Joystick(5);
  SwerveBaseModule drivebase = new SwerveBaseModule(driver_controller);

  public Robot() {
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {

    drivebase.gyro.reset();
    drivebase.current_state = DriveBaseStates.TEST_STEER;
    

  }

  @Override
  public void testPeriodic() {
    drivebase.update();
  }

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.SerialPort.Port;

import frc.robot.datum.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final DifferentialDrive m_robotDrive
      = new DifferentialDrive(new PWMVictorSPX(0), new PWMVictorSPX(1));
  private final Joystick m_stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  int tick = 0;

  // This example shows how to interface with one of the datum sensors.
  // It uses the jSerialComm library to support communications. Enter the
  // port name as a string in the following constructor to establish the
  // communication link. The port name is the same one used by the host OS.
  // On Windows it will typically be 'COMx' where 'x' is 1, 2, 3, 4, etc.  
  // On Linux and MacOS it is typically '/dev/ttyACMx' or similar.  The 
  // value of 'x' here will be 0, 1, 2, 3, etc.  When the constructor is 
  // called it prints a list of available ports to the console to aid in 
  // troubleshooting and debugging.
  //
  // The jSerialComm library also allows the datum sensors to be used in 
  // the simulator.  Simply enter the appropriate port name and execute 
  // 'WPILib: Simulate Robot Code on Desktop'.  The data sent from the 
  // sensor will be exactly the same as data sent when it is installed on
  // the robot.
  //  
  // Note that by using the jSerialComm library it is also possible to use
  // more than two USB serial devices at the same time on the roboRIO.  
  // Most USB hubs are supported by the roboRIO making it very easy to 
  // expand the number of sensors used.

  //DatumIMU datumIMU = new DatumIMU("/dev/ttyACM1");
  DatumIMU datumIMU = new DatumIMU("COM5");

  // Comment out any previous declarations and uncomment this declaration
  // to use the WPILib SerialPort library.  The communication library is
  // automatically chosen based on the type passed into the constructor.

  //DatumIMU datumIMU = new SerialPort(Port.kUSB1);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  // The following is necessary to trigger the method monitoring incoming
  // data on the serial port.  Command based project will already have 
  // this implemented.  It is included here to demonstrate using this 
  // capability in a TimedRobot project.

  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      m_robotDrive.stopMotor(); // stop robot
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(m_stick.getY(), m_stick.getX());

    double timestamp = datumIMU.getTimestamp();
    DatumIMU.DataPacket acc = datumIMU.getAccelerometer();
    DatumIMU.DataPacket gyro = datumIMU.getGyro();
    DatumIMU.DataPacket mag = datumIMU.getMagnetometer();

    System.out.print(tick++ + "  " + timestamp + "  ");
    System.out.print(acc.t + "  " + acc.x  + "  " + acc.y  + "  "  + acc.z  + "  ");
    System.out.print(gyro.t + "  " + gyro.x  + "  " + gyro.y  + "  "  + gyro.z + "  ");
    System.out.println(mag.t + "  " + mag.x  + "  " + mag.y  + "  "  + mag.z);
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}

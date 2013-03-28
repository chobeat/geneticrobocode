package trainbot;

/*
 * this robot will be used for training using hill climbing
 * values to train are in "value to train section"
 * please refer to my thesis for more information
 * author Firdan Machda 
 * based on algorithm/strategies by Mark Whitley @
 * http://mark.random-article.com/
 */
import java.awt.Color;
import java.awt.geom.Point2D;

import java.io.*;

import main.ChromoHolder;

import org.jgap.IChromosome;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.xml.XMLManager;

import robocode.AdvancedRobot;
//import robocode.Bullet;
//import robocode.HitRobotEvent;
//import robocode.HitWallEvent;
import robocode.*;

public class TrainStoopidbot extends AdvancedRobot {

	// value to train ~Original
	/*
	 * private int turnRemain = 10; private int wallMargin = 60; private int
	 * fireDistance = 400; private int strafingConstant= 150; private int
	 * initScanDegree =30; private int limitMiss = 5;
	 */
	public TrainStoopidbot() {

	}
	//set the parameters for the robot, reading from a Chromosome
	private void tuneToChromosomeFromFile() {
		//get the Chromosome from the ChromoHolder singleton
		IChromosome chromo = ChromoHolder.getChromo();
		IntegerGene turnGene = (IntegerGene) chromo.getGene(0);
		IntegerGene wallGene = (IntegerGene) chromo.getGene(1);
		IntegerGene fireGene = (IntegerGene) chromo.getGene(2);
		IntegerGene strafingGene = (IntegerGene) chromo.getGene(3);
		IntegerGene scanGene = (IntegerGene) chromo.getGene(4);
		IntegerGene limitGene = (IntegerGene) chromo.getGene(5);
		
		turnRemain = (Integer) turnGene.getAllele();
		wallMargin = (Integer) wallGene.getAllele();;
		fireDistance = (Integer) fireGene.getAllele();;
		strafingConstant = (Integer) strafingGene.getAllele();;
		initScanDegree = (Integer) scanGene.getAllele();;
		limitMiss = (Integer) limitGene.getAllele();;

	}

	private int turnRemain = 0;
	private int wallMargin = 0;
	private int fireDistance = 0;
	private int strafingConstant = 0;
	private int initScanDegree = 0;
	private int limitMiss = 0;

	private AdvancedEnemyBot enemy = new AdvancedEnemyBot();
	private byte moveDirection = 1;
	private byte scanDirection = 1;

	private int bulletMiss = 0;

	// add custom event wall avoider

	private int nearWall = 0;

	private boolean stop = false;

	public void run() {
		tuneToChromosomeFromFile();

		setBodyColor(Color.red);
		setGunColor(Color.blue);
		setRadarColor(Color.black);

		addCustomEvent(new Condition("tooCloseToWalls") {
			public boolean test() {
				return ((getX() <= wallMargin
						|| getX() >= getBattleFieldWidth() - wallMargin
						|| getY() <= wallMargin || getY() >= getBattleFieldHeight()
						- wallMargin));

			}
		});

		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		enemy.reset();
		while (true) {
			doScan();
			doStrafing();

			if (bulletMiss > limitMiss && bulletMiss < limitMiss * 2) {
				doNormalAttack();
			} else {
				doPredictAttack();
			}

			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		// tracking nearest enemy
		if (enemy.none() || e.getDistance() < enemy.getDistance() - 70
				|| e.getName().equals(enemy.getName())) {
			enemy.update(e, this);
		}

	}

	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("tooCloseToWall")) {
			if (nearWall <= 0) {
				nearWall += wallMargin;
				stop = true;
				setMaxVelocity(0); // break

			}
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}

	void doScan() {

		if (enemy.none())
			setTurnRadarRight(36000);
		else {
			double turningRadar = getHeading() - getRadarHeading()
					+ enemy.getBearing();
			turningRadar += initScanDegree * scanDirection;
			setTurnRadarRight(normalizeBearing(turningRadar));
			scanDirection *= -1;
		}
	}

	void doStrafing() { // strafing and getting closer

		setTurnRight(normalizeBearing(enemy.getBearing() + 90
				- (15 * moveDirection)));
		// changing strafe
		if (getTime() % 20 == 0) {
			moveDirection *= -1;
			setAhead(strafingConstant * moveDirection);
		}

		if (nearWall > 0)
			nearWall--;

		// normal movement: switch directions if we've stopped
		if (getVelocity() == 0 && stop) {
			setMaxVelocity(8);
			moveDirection *= -1;
			setAhead(100 * moveDirection);
			stop = false;
		}
	}

	public void onBulletMissed(BulletMissedEvent bullet) {
		bulletMiss++;
		if (bulletMiss > limitMiss * 2) {
			bulletMiss = 0;
		}
	}

	void doNormalAttack() {
		out.println(" i am doing normal attack " + bulletMiss);
		if (enemy.getDistance() < 500)
			setTurnGunRight(normalizeBearing(getHeading() - getGunHeading()
					+ enemy.getBearing()));

		// avoiding premature shooting
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < turnRemain)
			setFire(Math.min(400 / enemy.getDistance(), 3));

	}

	void doPredictAttack() {
		out.println(" i am doing predict attack " + bulletMiss);

		// if enemy not exist return
		if (enemy.none())
			return;

		// predictive targeting
		// calculate fire power based on distance
		double firePower = Math.min(fireDistance / enemy.getDistance(), 3);

		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;

		long time = (long) (enemy.getDistance() / bulletSpeed);
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);

		// turn gun to predicted location
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

		// avoiding premature shooting
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < turnRemain)
			setFire(Math.min(400 / enemy.getDistance(), 3));

	}

	double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double x0 = x2 - x1;
		double y0 = y2 - y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(x0 / hyp));
		double bearing = 0;

		if (x0 > 0 && y0 > 0) {
			bearing = arcSin;
		} else if (x0 < 0 && y0 > 0) {
			bearing = 360 + arcSin;
		} else if (x0 > 0 && y0 < 0) {
			bearing = 180 - arcSin;
		} else if (x0 < 0 && y0 < 0) {
			bearing = 180 - arcSin;
		}
		return bearing;
	}

}

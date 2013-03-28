package main

import robocode.control._
import robocode.control.events.BattleCompletedEvent
import robocode.control.events.BattleAdaptor
import robocode.BattleResults
import org.jgap.Chromosome
import org.jgap.IChromosome
import scala.util.Random
import trainbot.TrainStoopidbot
import org.jgap.xml.XMLManager
import java.nio._
import java.io.PrintWriter
import java.io.File
import org.jgap.xml.XMLDocumentBuilder
import org.jgap.data.DataTreeBuilder
import org.w3c.dom.Document
import robocode.control.events.BattleMessageEvent
import org.jgap.event.EventManager
import org.jgap.event.GeneticEvent
class Tester(round: Int, x: Int, y: Int) {
  def this() = {
    this(5, 800, 600)

  }

  val engine = new RobocodeEngine
  val battlefield = new BattlefieldSpecification(x, y)
  val myRobot = engine.getLocalRepository("trainbot.TrainStoopidbot*")
  val robots = myRobot :+ getRandomRobot(engine)

  val battleSpec = new BattleSpecification(round, battlefield, robots)

  def start_test(chromosome: Chromosome): Int = {
    writeChromosome(chromosome)
    val obs = new BattleObserver(chromosome)
    engine.addBattleListener(obs)

    engine.runBattle(battleSpec, true)

    engine.close()
    EventManagerHolder.eventManager.fireGeneticEvent(new GeneticEvent(GeneticEvent.GENOTYPE_EVOLVED_EVENT, chromosome))

    val reward = calcReward(obs)
    println(reward)
    reward

  }
  //reward value calculator
  def calcReward(obs: BattleObserver): Int = obs.results(0).getScore()

  //print all the robots of the tester
  def printRobots(): Unit = for (i <- robots) println("Robot: " + i.getName())

  //get a random 1vs1 robot from repo
  def getRandomRobot(eng: RobocodeEngine): RobotSpecification = {
    var result: RobotSpecification = null
    while (result == null || result.getName().contains("samplex") || result.getName().contains("sampleteam") || result.getName().contains("train")) {
      val a = eng.getLocalRepository()

      val rand = new Random(System.currentTimeMillis());
      val random_index = rand.nextInt(a.length);
      result = a(random_index);
     // println("Picked " + result.getName());
    }
    result
  }

  def printresults(results: Array[BattleResults]) {
    for (result <- results) {
      println("  " + result.getTeamLeaderName() + ": " + result.getScore());
      println(result.getLastSurvivorBonus())
      println(result.getSurvival())
    }

  }

  def writeChromosome(chromosome: Chromosome) = {
    /*     def builder=new XMLDocumentBuilder
    def tree=DataTreeBuilder.getInstance()
    def doc=tree.representChromosomeAsDocument(chromosome)
    def xmldoc=builder.buildDocument(doc) 
    println("Write :"+chromosome.getGenes().length)
      XMLManager.writeFile(xmldoc.asInstanceOf[Document], new File("chromo.xml" ))
    */
    //set Chromosome to be passed to the Robot
    ChromoHolder.setChromo(chromosome)
  }

}

class BattleObserver(chromo: Chromosome) extends BattleAdaptor {
  var results: Array[BattleResults] = Array()
  override def onBattleMessage(e: BattleMessageEvent) {
    // println(e.getMessage())

  }
  override def onBattleCompleted(e: BattleCompletedEvent) {
    results = e.getSortedResults()
    for (i <- results if i.getTeamLeaderName().contains(classOf[TrainStoopidbot].getName())) {
      val data = chromo.getApplicationData().asInstanceOf[RobotChromosomeApplicationData]
      val rank=i.getRank()
      if(chromo.getApplicationData()==null)
      {
        chromo.setApplicationData(new RobotChromosomeApplicationData(0,0))
        
      }
       val d = chromo.getApplicationData().asInstanceOf[RobotChromosomeApplicationData]
       
      if(i==1)
              chromo.setApplicationData(d w)
    	else
              chromo.setApplicationData(d l)
         
    }
  }

}

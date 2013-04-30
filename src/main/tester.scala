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

class EasyTester() extends Tester(Array(RoboRepo.getRandomRobot(RoboRepo.engine))){  }
class MediumTester() extends Tester(RoboRepo.engine.getLocalRepository("sample.VelociRobot")){}
class HardTester() extends Tester(RoboRepo.engine.getLocalRepository("ab.DengerousRoBatra*")){}

class SniperTester() extends Tester(RoboRepo.engine.getLocalRepository("sniper1.Sniper1*")){}
class RaptureTester() extends Tester(RoboRepo.engine.getLocalRepository("rapture.Rapture*")){}



class Tester(sparringPartners:Array[RobotSpecification],rounds: Int=5, x: Int=800, y: Int=600,name:Int=Random.nextInt(50)) {
  
  
  val engine= new RobocodeEngine
  val battlefield = new BattlefieldSpecification(x, y)
  val myRobot = engine.getLocalRepository("trainbot.TrainStoopidbot*")
  val robots = myRobot ++ sparringPartners
 

  def start_test(chromosome: Chromosome,obs_t:BattleObserver=null): Int = {
	writeChromosome(chromosome)
    val battleSpec=new BattleSpecification(rounds,battlefield,robots)
    
    	val obs = if(obs_t!=null)obs_t else new BattleObserver(chromosome)
    engine.addBattleListener(obs)
    engine.runBattle(battleSpec, true)
    engine.close()
    val reward = calcReward(obs)
    
    engine.removeBattleListener(obs)
    reward

  }
  //reward value calculator
  def calcReward(obs: BattleObserver): Int = { 
    val result=(obs.results.filter(x=>x.getTeamLeaderName().contains("Train")))(0)
    val name=result.getTeamLeaderName()
    
    assert(name.contains("Train"))
    
    val score=result.getScore()+result.getSurvival()	
   
    
	val square = Math.pow(score,2)
	if(result.getRank()==1)
		    (1.3*square).toInt
		    else
		      square.toInt
    
  }
  //print all the robots of the tester
  def printRobots(): Unit = for (i <- robots) println("Robot: " + i.getName())


  def printresults(results: Array[BattleResults]) {
    for (result <- results) {
      println("  " + result.getTeamLeaderName() + ": " + result.getRank());
      println(result.getScore())
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
  override def toString():String={
  "Tester: "+this.name.toString()
  }
  
}

class BattleObserver(chromo: Chromosome) extends BattleAdaptor {
  var results: Array[BattleResults] = Array()
  override def onBattleMessage(e: BattleMessageEvent) {
  //  println(e.getMessage())

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
       
      if(rank==1)
              chromo.setApplicationData(d w)
    	else
              chromo.setApplicationData(d l)
       
    
    }
  }}
object RoboRepo{
  
  val engine = new RobocodeEngine
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
}

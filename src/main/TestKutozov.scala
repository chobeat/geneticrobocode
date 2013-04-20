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


object TestKutozov {

  
def main(args: Array[String]): Unit = {   

		val engine= new RobocodeEngine
  	
		val battlefield = new BattlefieldSpecification(800, 600)
  val myRobot = engine.getLocalRepository("trainbot.TrainStoopidbot*")
  val robots = engine.getLocalRepository("trainbot.Kutozov*, sample.SittingDuck")
	
		
		val battleSpec=new BattleSpecification(5,battlefield,robots)
    
    val obs = new TestObserver()
	engine.setVisible(true)
	engine.addBattleListener(obs)
    engine.runBattle(battleSpec, true)
    engine.close()
    
    engine.removeBattleListener(obs)
		
		
}
  

  
}


class TestObserver() extends BattleAdaptor {
  var results: Array[BattleResults] = Array()
  override def onBattleMessage(e: BattleMessageEvent) {
  println(e.getMessage())

  }
  override def onBattleCompleted(e: BattleCompletedEvent) {
    results = e.getSortedResults()
   
  }}
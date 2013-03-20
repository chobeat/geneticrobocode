package main

import robocode.control._
import robocode.control.events.BattleCompletedEvent
import robocode.control.events.BattleAdaptor
import robocode.BattleResults
import org.jgap.Chromosome
import org.jgap.IChromosome

class Tester(chromosome:IChromosome,round:Int,x:Int,y:Int) {
	def this(chromosome:IChromosome)={
	  this(chromosome,5,800,600)
	  
	}
  
  
  def start_test():Int={ 
    
   println(chromosome.getGenes)
   
    val engine = new RobocodeEngine
   val battlefield = new BattlefieldSpecification(x, y)
   val rob = engine.getLocalRepository()
   val robots=rob++rob
    val obs = new BattleObserver()
   engine.addBattleListener(obs)
 
   val battleSpec = new BattleSpecification(round, battlefield, robots)
   engine.runBattle(battleSpec, true)
   
   
   engine.close()
   calcReward(obs)
  }
  def calcReward(obs:BattleObserver):Int=obs.results(0).getScore()+obs.results(0).getSurvival()
   
  
  
  def printresults(results:Array[BattleResults]){
    for (result <- results) {
      println("  " + result.getTeamLeaderName() + ": " + result.getScore());
      println(result.getLastSurvivorBonus())
      println(result.getSurvival())
    }
    
  }
  
  
}

class BattleObserver() extends BattleAdaptor {
  var results:Array[BattleResults]=Array()
  
  override def onBattleCompleted(e: BattleCompletedEvent) {
 
  results=e.getIndexedResults()
  
  }

}

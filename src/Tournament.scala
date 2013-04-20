import robocode.control.RobocodeEngine
import scala.collection.JavaConverters._
import robocode.control.RobotSpecification
import robocode.control.events.IBattleListener
import robocode.control.BattleSpecification
import robocode.control.BattlefieldSpecification
import com.thoughtworks.xstream.XStream
import robocode.control.events.BattleErrorEvent
import robocode.control.events.BattleMessageEvent
import robocode.control.events.BattleAdaptor
import robocode.BattleEndedEvent
import robocode.control.events.BattleCompletedEvent

class Tournament {

  val engine = new RobocodeEngine
  val robots = engine.getLocalRepository()
  val couples = createCouples(robots.toList)

  val battleListener = new TournamentListener(engine)
  engine.addBattleListener(battleListener)
  val battlefieldSpec = new BattlefieldSpecification(800, 600)
  def fight(couple: (RobotSpecification, RobotSpecification)): RobotSpecification = {
    println("Fight between %s and %s".format(couple._1.getName(), couple._2.getName()))
    val battleSpec = new BattleSpecification(1, battlefieldSpec, Array(couple._1, couple._2))
    battleListener.winner = null
    battleListener.first = couple._1
    battleListener.second = couple._2
    
    engine.setVisible(false)
    engine.runBattle(battleSpec)
    engine.close()
    
    battleListener.first = null
    battleListener.second = null
    println("Winner "+battleListener.winner.getName())
    battleListener.winner
  }

  def createCouples(l: List[RobotSpecification]): List[(RobotSpecification, RobotSpecification)] = {
    val list = l.filter(x => !(x.getName().contains("sampleex") || (x.getName().contains("sampleteam"))))

    scala.util.Random.shuffle(list)
    def createCouple(l: List[RobotSpecification]): List[(RobotSpecification, RobotSpecification)] = {
      l match {
        case Nil => Nil
        case a :: b :: rest => Pair(a, b) :: createCouple(rest)
        case a :: Nil => List(Pair(a, a))

      }

    }

    createCouple(list)
  }

  def fightStage(curCouples: List[(RobotSpecification, RobotSpecification)]): RobotSpecification = {

    val unfilteredRobots = for (i <- curCouples) yield {

      fight(i)

    }
    val nextRobots = unfilteredRobots.filter(_ != null)

    if (nextRobots.length <= 1)
      nextRobots(0)

    else
      fightStage(createCouples(nextRobots))

  }

}

class TournamentListener(engine: RobocodeEngine) extends BattleAdaptor {

  var results = Array()
  var winner: RobotSpecification = null
  var first:RobotSpecification=null
  var second:RobotSpecification=null
  val xstream=new XStream
  override def onBattleError(e: BattleErrorEvent) {println(e.getError())
	  
    
  }
  override def onBattleCompleted(event: BattleCompletedEvent) {
    
  determineWinner(event)   
  logBattle(event)
  
  }
  
  def logBattle(event:BattleCompletedEvent){
	  xstream.toXML(3)
    println()
  }
  
  def determineWinner(e:BattleCompletedEvent){ //follia immonda 
    val results=e.getIndexedResults()
    winner=null
    val w=for(i<-results if  i.getRank()==1)yield i
    val winnerName=w(0).getTeamLeaderName().replace("*","")
    assert(w(0).getRank()==1) 
    println(winnerName)
    println(first.getName())
    val cleanWinnerName= if(winnerName.contains(first.getName().replace("*","")))first.getName() else second.getName()
    winner = engine.getLocalRepository(cleanWinnerName)(0)

    assert(winner!=null&&cleanWinnerName==winner.getName()&&( ( winner.getName()==first.getName())||winner.getName()==second.getName() ))
        
}
  
  override def onBattleMessage(e: BattleMessageEvent) {}

}

object Main {

  def main(args: Array[String]): Unit = {
    val t = new Tournament
    println(t.fightStage(t.couples).getName())
  
  }

}
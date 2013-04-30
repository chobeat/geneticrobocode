
import main.RobotChromosomeApplicationData
import main.RoboRepo
import robocode.control.RobocodeEngine
object Toy{
  
def main(args: Array[String]): Unit = {   
		val eng=new RobocodeEngine
		eng.getLocalRepository() foreach(x=>if(x.getName().contains("ED4"))println(x.getName()))

}
  


   
}


import main.RobotChromosomeApplicationData
import main.RoboRepo
object Toy{
  
def main(args: Array[String]): Unit = {   

		RoboRepo.engine.getLocalRepository() foreach(x=>println(x.getName()))
		
}
  

   
}

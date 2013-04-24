
import main.RobotChromosomeApplicationData
import main.RoboRepo
object Toy{
  
def main(args: Array[String]): Unit = {   

  println(adjustAngles(350,170))
}
  

    def adjustAngles(aa1: Int, aa2: Int):(Int,Int)= {
		def convergenceRate=0.5
      var diff = aa2 - aa1;
      var a1 = aa1
      var a2 = aa2
      if (diff > 180) {
        diff -= 360;
      } else if (diff < -180) {
        diff += 360;
      }

      a1 = ((a1 + diff / 2 * convergenceRate) % 360).toInt; // valutare gli effetti del casting sulla precisione
      if (a1 < 0) {
        a1 += 360;
      }

      a2 = ((a2 - diff / 2 * convergenceRate) % 360).toInt
      if (a2 < 0) {
        a2 += 360;
      }
      (a1, a2)
    }

   
}

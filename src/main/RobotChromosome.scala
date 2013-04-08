package main
import org.jgap.Chromosome
import org.jgap.Configuration
import org.jgap.FitnessFunction
import org.jgap.IChromosome
import org.jgap.Gene
import org.jgap.event.GeneticEventListener
import org.jgap.event.GeneticEvent
import org.jgap.event.EventManager
import scala.util.Random

class RobotChromosome(conf: Configuration, g: Array[Gene]) extends Chromosome(conf, g) {
	
	

}


//Keeps track of the stats of a single chromosome. The chromosome is by default identified by a random int

class RobotChromosomeApplicationData(won:Double,tot:Int, name:Int=Random.nextInt()){
	
  def perc=won/tot*100
  def w:RobotChromosomeApplicationData={ new RobotChromosomeApplicationData(won+1,tot+1,name)}
  def l:RobotChromosomeApplicationData={  new RobotChromosomeApplicationData(won,tot+1,name)}

  override def toString():String={
   "The chromosome %s played %d times with a percentage of %4.1f%s".format(name,tot,perc,"%")
    
  }
}

//Fitness function calculator
class RobotFitnessFunction(t:Tester) extends FitnessFunction {

  override def evaluate(c: IChromosome): Double = {
    //main test on a single chromosome
    
    var res=t.start_test(c.asInstanceOf[Chromosome])
    println(res)
    res
  }
  
  

}

class EvolutionListener extends GeneticEventListener{
  override def geneticEventFired(e:GeneticEvent)={
    def ad=e.getSource().asInstanceOf[Chromosome].getApplicationData().asInstanceOf[RobotChromosomeApplicationData]
  
  }
  
  
}
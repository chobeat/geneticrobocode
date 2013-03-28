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

class RobotChromosomeApplicationData(won:Double,tot:Int, name:Int=Random.nextInt()){
	
	
  def w:RobotChromosomeApplicationData={ new RobotChromosomeApplicationData(won+1,tot+1,name)}
  def l:RobotChromosomeApplicationData={  new RobotChromosomeApplicationData(won,tot+1,name)}

  override def toString():String={
   "The chromosome %s played %d times with a percentage of %4.1f%s".format(name,tot,won/tot*100,"%")
    
  }
}

//Fitness function calculator
class RobotFitnessFunction(t:Tester) extends FitnessFunction {

  override def evaluate(c: IChromosome): Double = {
    //main test on a single chromosome
    t.start_test(c.asInstanceOf[Chromosome])
    
  }
  
  

}

class EvolutionListener extends GeneticEventListener{
  override def geneticEventFired(e:GeneticEvent)={
    println("Evento: "+e.getEventName()+" valore:"+e.getSource())
    
  
  }
  
  
}
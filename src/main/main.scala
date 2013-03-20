package main
import org.jgap.Chromosome
import org.jgap.Configuration
import org.jgap.impl.NumberGene
import org.jgap.impl.DoubleGene
import org.jgap.impl.DefaultConfiguration
import org.jgap.Genotype
object Main {

  def main(args: Array[String]): Unit = {
    val conf=new DefaultConfiguration
    val fitfunc=new RobotFitnessFunction
    	
    conf.setFitnessFunction(fitfunc)
    println(conf.getFitnessFunction())
    val genes=for(i<-1 to 5) yield new DoubleGene(conf,0,i)
    val chromo=new RobotChromosome(conf,genes.toArray)
    conf.setSampleChromosome(chromo)
    conf.setPopulationSize(200)
    val pop=Genotype.randomInitialGenotype(conf)
    pop.evolve(100)
    
    val best=pop.getFittestChromosome()
    
    
    println(best.getGene(0))
    
    
  }

}
  
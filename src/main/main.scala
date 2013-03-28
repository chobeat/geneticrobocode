package main
import org.jgap.Chromosome
import org.jgap.Configuration
import org.jgap.impl.NumberGene
import org.jgap.impl.DoubleGene
import org.jgap.impl.DefaultConfiguration
import org.jgap.Genotype
import trainbot.TrainStoopidbot
import org.jgap.impl.IntegerGene
import org.jgap.event.EventManager
import org.jgap.event.GeneticEvent
import org.jgap.impl.BestChromosomesSelector
import org.jgap.Population
import main.ConfHolder$
import org.jgap.Gene
import main.ConfHolder$
object Main {

  def main(args: Array[String]): Unit = {
    val p=new Population(ConfHolder.conf)
    
  evolvePop(p)
    System.exit(0)

  }
  def evolvePop(p:Population)={
    
		  
    val conf = ConfHolder.conf 
    println(conf)
    val em = EventManagerHolder.eventManager
    val fitfunc = new RobotFitnessFunction(new Tester())
    conf.setFitnessFunction(fitfunc)
    val genes = genesDefinition
    val chromo = new RobotChromosome(conf, genes.toArray)
    
    conf.setSampleChromosome(chromo)
    conf.setPopulationSize(2)

    val pop = Genotype.randomInitialGenotype(conf)
    em.addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new EvolutionListener)

    pop.evolve(3)
    val best=pop.getFittestChromosome()
    println(best.getApplicationData())
    
  }
  def genesDefinition: IndexedSeq[Gene]={
    for (i <- 1 to 6) yield new IntegerGene(ConfHolder.conf, 0, 30)
    
    
  }
}
  
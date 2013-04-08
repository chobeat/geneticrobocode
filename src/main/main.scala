package main
import org.jgap.Chromosome
import scala.collection.JavaConverters._
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
import org.jgap.Gene
import org.jgap.FitnessFunction
import org.jgap.distr.Breeder
import org.jgap.BreederBase
import org.jgap.IChromosome
import scala.collection.mutable.ListBuffer
import org.jgap.NaturalSelector
import scala.util.Random
object Main {

  def main(args: Array[String]): Unit = {
    def POP_SIZE = 10
    val testers = List(

      (new MediumTester, 10),
      (new HardTester, 50),
      (new MediumTester, 10))
    val best = breed(testers, POP_SIZE)
    println(best)
    System.exit(0)

  }

  def breed(funcList: List[(Tester, Int)], popSize: Int): IChromosome = {
    var best: Array[IChromosome] = null
    		println("sss")
    /*For every Tester, i pick the best chromosome for the previous evolution and put it in the new genotype
    *The new genotype is filled with random chromosomes
    *In the first run, it's populated with random chromosomes 
    */
    for ((func, iterations) <- funcList) {
      Configuration.reset()
      val conf: Configuration = my_conf(new RobotFitnessFunction(func), popSize)
      val genotype = best match {
        case (null) => Genotype.randomInitialGenotype(conf)
        case (_) => new Genotype(conf, new Population(conf, best.toArray))
      }
      genotype.evolve(iterations)
      best = toChromoArray(genotype)

    }

    best(0)

  }

  def toChromoArray(genotype: Genotype): Array[IChromosome] = {

    val l = genotype.getChromosomes().toList
    l.map(x => x.getFitnessValue()).foldLeft(Math.MIN_DOUBLE)((i, m) => m.max(i))
    l.slice(0, 4).toArray

  }
  //creates a new configuration given a fitness function
  def my_conf(fitFunc: FitnessFunction, popSize: Int): Configuration = {
    
    
    val conf = new DefaultConfiguration
    conf.setName(Random.nextInt().toString)
    conf.setMinimumPopSizePercent(25)
    conf.setFitnessFunction(fitFunc)
    conf.setKeepPopulationSizeConstant(true)
    conf.setPopulationSize(popSize)
    val genes = genesDefinition(conf)
    val chromo = new RobotChromosome(conf, genes.toArray)
    conf.setSampleChromosome(chromo)
   
    conf
  }

  def genesDefinition(conf: Configuration): IndexedSeq[Gene] = {

    def geneconf = Array(
      //scanDegree gene 
      new IntegerGene(conf, 0, 360),

      //wall margin
      new IntegerGene(conf, 1, 200),

      //fireDistance
      new IntegerGene(conf, 100, 1000),

      //strafing
      new IntegerGene(conf, 100, 300),
      //turn remain
      new IntegerGene(conf, 1, 50),

      //limitMiss
      new IntegerGene(conf, 1, 15))
    geneconf
  }

}
  
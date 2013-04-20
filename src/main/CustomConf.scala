package main
import org.jgap.FitnessFunction
import org.jgap.impl._

import org.jgap._
import scala.util.Random
import org.jgap.impl.IntegerGene

class CustomConf(fitFunc: FitnessFunction, popSize: Int) extends DefaultConfiguration(){
    setName(Random.nextInt().toString)
    setMinimumPopSizePercent(25)
    setFitnessFunction(fitFunc)
    setKeepPopulationSizeConstant(true)
    setPopulationSize(popSize)	
    val genes = GenesHolder.genesDefinition(this)
    val chromo = new RobotChromosome(this, genes)
    setSampleChromosome(chromo)
    addGeneticOperator(new org.jgap.impl.AveragingCrossoverOperator(this))
    addNaturalSelector(new WeightedRouletteSelector(this), true)

}

object GenesHolder{
def genesDefinition(conf: Configuration): Array[Gene] = {

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
    geneconf.toArray
  }
}

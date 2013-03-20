package main
import org.jgap.Chromosome
import org.jgap.Configuration
import org.jgap.FitnessFunction
import org.jgap.IChromosome
import org.jgap.Gene

class RobotChromosome(conf: Configuration, g: Array[Gene]) extends Chromosome(conf, g) {

  def testChromosomeFitness(): Unit = {
    println(this.getGene(0))

  }

}
class RobotFitnessFunction extends FitnessFunction {

  override def evaluate(c: IChromosome): Double = {
    val t = new Tester(c)
    t.start_test()

  }

}
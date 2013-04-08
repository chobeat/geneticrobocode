package main
import org.jgap.IChromosome
import org.jgap.Configuration
import org.jgap.Chromosome
import org.jgap.event.EventManager
import org.jgap.impl.DefaultConfiguration

//Singleton for communication purposes. Centralize chromosomes to bypass Robocode Security
object ChromoHolder {
  var chromosome: Chromosome = null
  def setChromo(c: Chromosome) = {
    chromosome = c
  }
  def getChromo() = {
    println(chromosome.size())
    chromosome
  }
}


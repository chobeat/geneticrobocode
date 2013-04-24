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
import org.jgap.xml.XMLManager
import java.io.FileWriter
import org.jgap.xml.XMLDocumentBuilder
import org.jgap.data.DataTreeBuilder
import org.w3c.dom.Document
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.FileInputStream
import scala.xml.XML
import java.util.Date
import org.jgap.impl.WeightedRouletteSelector
object Main {
  def chromesDir = "generatedChromosomes"
  def fs = new FS(chromesDir)
  def MAX_LOCAL_POP = 15

  def POP_SIZE = 10
  val testers = List(

    (new MediumTester, 1),
    (new HardTester, 1),
    (new MediumTester, 1))
  def main(args: Array[String]): Unit = {
    /*  val best = breed(testers, POP_SIZE)
   fs.storeChromo(best)
     */
    expandLocalChromes()
    
    System.exit(0)

  }

  def expandLocalChromes() {

    while (fs.countChromes() < MAX_LOCAL_POP) {
      val best = breed(testers, POP_SIZE, fs.getAllGeneratedChromes())
      for(i<-best)fs.storeChromo(i)
      
    }

  }

  def breed(funcList: List[(Tester, Int)], popSize: Int, chromePop: Array[IChromosome] = null): Array[IChromosome] = {
    var best: Array[IChromosome] = chromePop
    /*For every Tester, i pick the best cdef a:Int=get
  hromosome for the previous evolution and put it in the new genotype
    *The new genotype is filled with random chromosomes
    *In the first run, it's populated with random chromosomes 
    */

    for ((func, iterations) <- funcList) {
      Configuration.reset()
      val conf: Configuration = new CustomConf(new RobotFitnessFunction(func), popSize)

      val genotype = best match {
        case (null) => Genotype.randomInitialGenotype(conf)
        case (Array()) => Genotype.randomInitialGenotype(conf)
        case (_) => new Genotype(conf, new Population(conf, best.toArray))
      }
      genotype.fillPopulation(POP_SIZE)
      genotype.evolve(iterations)
      best = toChromoArray(genotype)

    }
    (best(0)::best(1)::best(2)::List()).toArray
  }

  def toChromoArray(genotype: Genotype): Array[IChromosome] = {

    val l = genotype.getChromosomes().toList
    l.map(x => x.getFitnessValue()).foldLeft(Math.MIN_DOUBLE)((i, m) => m.max(i))
    l.slice(0, 4).toArray

  }

}

class FS(chromesDir: String) {
  val dir = new File(chromesDir)
  val conf = dummyconf.get
  def storeChromo(c: IChromosome) {

    val builder = new XMLDocumentBuilder
    val tree = DataTreeBuilder.getInstance()
    val doc = tree.representChromosomeAsDocument(c)
    val xmldoc = builder.buildDocument(doc)
    if (!dir.exists())
      dir.mkdir()

    val now = new Date
    XMLManager.writeFile(xmldoc.asInstanceOf[Document], new File("%s/%s.chr".format(chromesDir, now.getTime())))

  }

  def hasExt(f: File, ext: String): Boolean = f.getName().split('.').last == ext

  def countChromes(): Int = { dir.listFiles().filter(x => hasExt(x, "chr")).length }

  def mergeHistory() {
    val pop = new Genotype(conf, getAllGeneratedChromes())

    val builder = new XMLDocumentBuilder
    val tree = DataTreeBuilder.getInstance()
    val doc = tree.representGenotypeAsDocument(pop)
    val xmldoc = builder.buildDocument(doc)
    if (!dir.exists())
      dir.mkdir()

    val now = new Date
    XMLManager.writeFile(xmldoc.asInstanceOf[Document], new File("%s/%d-%s.mer".format(chromesDir, pop.getPopulation().size, now.getTime())))

  }
  def getAllGeneratedChromes(): Array[IChromosome] = {
    if (!dir.isDirectory()) {
      Array()

    }

    val list = for (f: File <- dir.listFiles() if hasExt(f, "chr")) yield XMLManager.getChromosomeFromDocument(conf, XMLManager.readFile(f))

    list.toArray

  }

}

object dummyconf {
  val c = new DefaultConfiguration();

  c.setSampleChromosome(new RobotChromosome(c, GenesHolder.genesDefinition(c)));
  c.setFitnessFunction(new RobotFitnessFunction(new MediumTester))
  c.setPopulationSize(1)
  def get(): Configuration = c

}

  
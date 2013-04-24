package main
import org.jgap.FitnessFunction
import org.jgap.impl._
import org.jgap._
import scala.util.Random
import org.jgap.impl.IntegerGene
import org.jgap.event.EventManager
import sun.security.util.Length

class CustomConf(fitFunc: FitnessFunction, popSize: Int) extends Configuration() {
  setName(Random.nextInt().toString)
  setMinimumPopSizePercent(25)
  setFitnessFunction(fitFunc)
  setKeepPopulationSizeConstant(true)
  setPopulationSize(popSize)
  val genes = GenesHolder.genesDefinition(this)
  val chromo = new RobotChromosome(this, genes)
  setRandomGenerator(new StockRandomGenerator)
  setEventManager(new EventManager());
  setFitnessEvaluator(new DefaultFitnessEvaluator)
  setSampleChromosome(chromo)
  println(getNaturalSelectors(true).size())

  addGeneticOperator(new org.jgap.impl.TwoWayMutationOperator(this, 3))
  addGeneticOperator(new CustomAverageOperator(this, 0.75, this.popSize / 3))
  addNaturalSelector(new BestChromosomesSelector(this), true)

}

class CustomAverageOperator(c: Configuration, convergenceRate: Double, cr: Int) extends CrossoverOperator(c, cr) {
  assert(convergenceRate > 0 && convergenceRate <= 1)

  override protected def doCrossover(c1: IChromosome, c2: IChromosome, a_candidateChromosomes: java.util.List[_], generator: RandomGenerator) {
    val g1 = c1.getGenes()
    val g2 = c2.getGenes()
    def size = g1.length
    println("Pop :" + c1.getConfiguration().getPopulationSize())
    g1 foreach (x => print(x.getAllele() + " "))
    println("")
    g2 foreach (x => print(x.getAllele() + " "))

    println("")
    println("---------------------")

    //angle gene
    def PI = Math.Pi
    def angleDifference(theta1: Integer, theta2: Integer) = {
      var difference = theta2 - theta1 // in range 

      difference.toInt
    }

    def getQuad(a: Int) = ((a - 1) % 360) / 90 + 1

    val apos = GenesHolder.angleLocus
    val a1 = Integer.valueOf(g1(apos).getAllele().toString())
    val a2 = Integer.valueOf(g2(apos).getAllele().toString())
    
    //TODO: RIFO

    /*
    * val adiff=angleDifference(a1,a2).toInt
    
    val aa:((Gene,Int),(Gene,Int))=if(a1<a2)((g1(0),a1),(g2(0),a2))else((g2(0),a2),(g1(0),a1))
    val scaledDiff=Math.abs((adiff/2*convergenceRate).toInt)
    
    *  if(Math.abs(adiff)<=180)
    	{aa._1._1.setAllele(aa._1._2+scaledDiff)
    	aa._2._1.setAllele(aa._2._2-scaledDiff)
    	}   else
    	{aa._1._1.setAllele(aa._1._2-scaledDiff)
    	aa._2._1.setAllele(aa._2._2+scaledDiff)
    	}
    */

    def adjustAngles(aa1: Int, aa2: Int):(Int,Int)= {

      var diff = aa2 - aa1;
      var a1 = aa1
      var a2 = aa2
      if (diff > 180) {
        diff -= 360;
      } else if (diff < -180) {
        diff += 360;
      }

      a1 = ((a1 + diff / 2 * convergenceRate) % 360).toInt; 
      if (a1 < 0) {
        a1 += 360;
      }

      a2 = ((a2 - diff / 2 * convergenceRate) % 360).toInt
      if (a2 < 0) {
        a2 += 360;
      }
      (a1, a2)
    }

    
    val newAngles=adjustAngles(a1,a2)
    g1(0).setAllele(newAngles._1)
    g2(0).setAllele(newAngles._2)
    
    
    for (i <- 0 to size - 1 if i != GenesHolder.angleLocus) {

      def v1 = Integer.valueOf(g1(i).getAllele().toString())
      def v2 = Integer.valueOf(g2(i).getAllele().toString())

      def rescal = (Math.abs(v1 - v2) / 2 * convergenceRate).toInt
      if (v1 < v2) {

        g1(i).setAllele(v1 + rescal)
        g2(i).setAllele(v2 - rescal)

      } else {

        g1(i).setAllele(v1 - rescal)
        g2(i).setAllele(v2 + rescal)
      }

    }
    c1.getGenes() foreach (x => print(x.getAllele() + " "))
    println("")

    c2.getGenes() foreach (x => print(x.getAllele() + " "))
    println("")
    println("++++++++++++++++")

  }

}

object GenesHolder {
  def angleLocus = 0
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

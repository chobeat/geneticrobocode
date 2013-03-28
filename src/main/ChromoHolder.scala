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
  def getChromo() = chromosome

}

//Singleton for Global EventManager
object EventManagerHolder {
  private var _eventManager=new EventManager
  def eventManager=_eventManager
  

}
object ConfHolder{
   private var _conf: Configuration = null
  def setConf(c: Configuration) = { _conf = c }
	def conf:Configuration= {if(_conf ==null) _conf= new DefaultConfiguration; _conf}
  
}

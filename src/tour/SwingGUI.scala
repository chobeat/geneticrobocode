package tour
import scala.swing._
class TournamentGUI(tournament:Tournament) extends SimpleSwingApplication{
	def top = new MainFrame {
title = "Robocode Tournament"
contents = new Button {
text = "Click Me!"
}
}
	 override def startup(args: Array[String]) {
	  top.visible=true
	  
	}
  
}
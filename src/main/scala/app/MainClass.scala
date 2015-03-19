package app

import java.net.InetSocketAddress

import actor.AtomicService
import akka.actor.ActorSystem

/**
 * Created by ruguer on 3/18/15.
 */
object MainClass {

  def main(args:Array[String]) : Unit = {
    val system = ActorSystem("atomic-service-system")
    val endpoint = new InetSocketAddress("localhost", 11111)
    system.actorOf(AtomicService.props(endpoint), "echo-service")

    scala.io.StdIn.readLine(s"Hit ENTER to exit ...${System.getProperty("line.separator")}")
    system.shutdown()
  }
}

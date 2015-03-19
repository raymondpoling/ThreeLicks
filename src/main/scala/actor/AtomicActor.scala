package actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, Props}
import akka.io.Tcp
import akka.util.ByteString

/**
 * Created by ruguer
 * 3/18/15.
 */
class AtomicActor(ts:Map[String,Int]) extends Actor {
  var tickets : Map[String,AtomicInteger] = ts.map(i => i._1 -> new AtomicInteger(i._2))

  override def receive: Receive = {
    case key : String if tickets.contains(key) =>
      val t = tickets(key).getAndIncrement.toString
      sender ! Tcp.Write(
      ByteString("VALUE " + key + " " + ByteString(t).size + "\r\n" +
        t+ "\r\nEND\r\n"))
    case key : String => tickets = tickets + (key -> new AtomicInteger(0))
      self.tell(key,sender)
  }
}

object AtomicActor {
  def props(m:Map[String,Int]) : Props = Props(classOf[AtomicActor],m)
}

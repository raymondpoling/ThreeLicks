package actor

import java.net.InetSocketAddress
import java.util.concurrent.atomic.{AtomicLong, AtomicInteger}

import akka.actor.Actor.Receive
import akka.actor._
import akka.io.Tcp
import akka.util.ByteString

/**
 * Created by ruguer
 * 3/18/15.
 */
object AtomicConnectionHandler {
  def props(remote: InetSocketAddress, connection: ActorRef, atomicActor: ActorRef): Props =
    Props(new AtomicConnectionHandler(remote, connection, atomicActor))
}

class AtomicConnectionHandler(remote: InetSocketAddress, connection: ActorRef, atomicActor: ActorRef) extends Actor with ActorLogging {

  // We need to know when the connection dies without sending a `Tcp.ConnectionClosed`
  context.watch(connection)

  val ai = new AtomicLong(0)

  def receive: Receive = {
    case Tcp.Received(data) =>
      val text = data.utf8String.trim
      log.debug("Received '{}' from remote address {}", text, remote)
      text match {
        case t if t.startsWith("get ") =>  atomicActor.tell(t.substring(4),sender)
        case _ => sender ! Tcp.Write(ByteString("CLIENT ERROR '" + text + "' Invalid query\r\n"))
      }
    case _: Tcp.ConnectionClosed =>
      log.debug("Stopping, because connection for remote address {} closed", remote)
      context.stop(self)
    case Terminated(`connection`) =>
      log.debug("Stopping, because connection for remote address {} died", remote)
      context.stop(self)
  }
}
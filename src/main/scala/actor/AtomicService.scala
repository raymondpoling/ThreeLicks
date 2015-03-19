package actor

import java.net.InetSocketAddress

import akka.actor.{ActorLogging, Actor, Props}
import akka.io.{Tcp, IO}

/**
 * Created by ruguer
 * 3/18/15.
 */
object AtomicService {
  def props(endpoint: InetSocketAddress): Props =
    Props(new AtomicService(endpoint))
}

class AtomicService(endpoint: InetSocketAddress) extends Actor with ActorLogging {
  implicit val system = context.system

  val atomic = system.actorOf(AtomicActor.props(Map()))

  IO(Tcp) ! Tcp.Bind(self, endpoint)

  override def receive: Receive = {
    case Tcp.Connected(remote, _) =>
      log.debug("Remote address {} connected", remote)
      sender ! Tcp.Register(context.actorOf(AtomicConnectionHandler.props(remote, sender, atomic)))
  }
}
package core

import akka.actor.ActorSystem

trait Core {
  implicit def system: ActorSystem
}

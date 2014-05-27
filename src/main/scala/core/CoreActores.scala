package core

import akka.actor.Props
import api._

trait CoreActors{
  this: Core =>
    val testing = system.actorOf(Props[TestActor])
    val registering = system.actorOf(Props[RegisterActor])

}

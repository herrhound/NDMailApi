package models

case class Person (PersonId: Int, Name: Option[String] = None, Family: Option[String] = None, Age: Option[Int] = None)

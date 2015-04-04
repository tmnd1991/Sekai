package it.murgia.antonio.sekai.test.test

import it.murgia.antonio.sekai.base.Message
import it.murgia.antonio.sekai.base.Environments.impl.BasicEnvironment
import it.murgia.antonio.sekai.test.impl.{EchoActiveThing, EchoInterceptor}

/**
 * Created by tmnd91 on 04/04/15.
 */
import org.scalacheck.Properties

object EnvSpecification extends Properties("BasicEnvironment") {
  def time[A](f: => A) = {
    val s = System.nanoTime
    val ret = f
    println("time: "+(System.nanoTime-s)/1e6+"ms")
    ret
  }

  property("event") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.event(m)
      i.counter == 1
    }
  }

  property("signalFirst") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.signalFirst(m)
      i.counter == 1
    }
  }

  property("signalFirstWithResponse") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.signalFirstWithResponse[String, String](m) == "Prova" && i.counter == 1
    }
  }

  property("dispatch") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.dispatch(m, r)
      i.counter == 1
    }
  }

  property("signalAll") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.signalAll(m)
      i.counter == 1
    }
  }

  property("signalAllWithResponse") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.signalAllWithResponse[String, String](m) == Seq("Prova") && i.counter == 1
    }
  }

  property("invitation") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.invitation(m, r)
      i.counter == 1
    }
  }

  property("multicastRequest") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.multicastRequest[String, String](m, r) == Seq("Prova") && i.counter == 1
    }
  }

  property("unicastRequest") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.unicastRequest[String, String](m, r) == "Prova" && i.counter == 1
    }
  }

  property("allTogetherNow") = {
    time{
      implicit val environment = new BasicEnvironment
      val a = new EchoActiveThing()
      val i = new EchoInterceptor()
      val m = Message[String]("ProvaSubject","Prova")
      val r = "Echo"
      a.event(m)
      a.signalFirst(m)
      a.signalFirstWithResponse[String, String](m)
      a.dispatch(m, r)
      a.signalAll(m)
      a.signalAllWithResponse[String, String](m)
      a.invitation(m, r)
      a.multicastRequest[String, String](m, r)
      a.unicastRequest[String, String](m, r)
      i.counter == 9
    }
  }
}
import java.io.PrintStream
import java.net.InetAddress

import scala.io.BufferedSource
import scala.io._
import scala.tools.nsc.io
import scala.tools.nsc.io.Socket
import java.net._
import java.io._
import java.net.{Socket => JSocket}

import scala.collection.mutable


class Client(ip: String, port: Int) {
  val socket = new JSocket(ip, port)

  val primesNumbers = mutable.ArrayBuffer.empty[BigInt]

  var number: BigInt = 0
  var n: Int = 0

  var stop = false

}

object Client {

  def main(args: Array[String]): Unit = {

    println("Enter server ip address: ")
    val ip = StdIn.readLine()
    println("Enter port: ")
    val port = StdIn.readInt()
    val client: Client = new Client(ip, port)
    println("Connected to the Server")
    val clientThread: ClientThread = new ClientThread(client, client.socket)
    clientThread.start()

    // calculate thread
    new Thread(new Runnable {
      override def run(): Unit = {
        while (true) {
          client.stop = false
          if (client.number != 0) {
            var i = 0
            var ans = 0
            while (i < client.primesNumbers.size && ans == 0 && !client.stop) {
              if (client.primesNumbers(i) < client.n && client.number % client.primesNumbers(i) == 0)
                ans = 1
              i += 1
            }
            if (clientThread.ans != 1)
              clientThread.ans = ans
            client.number = 0
            clientThread.calculateDone = true
          }
          Thread.sleep(1)
        }
      }
    }).start()
  }
}

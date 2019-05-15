import java.io._
import java.net.Socket

import org.apache.log4j.Logger

import scala.collection.mutable
import scala.io.BufferedSource

class ServerThread(server: Server, clientConnection: Socket, cNumber: Int, logger: Logger) extends Thread {
  var state: STATE.Value = STATE.READY
  var clientNumber = cNumber
  var number: BigInt = null
  var setNumber: Boolean = false
  var ans = -1
  var endSending = false
  var sendPrime = false
  var prime: BigInt = null

  var clientMissed = false
  val arr = mutable.ArrayBuffer.empty[BigInt]

  override def run(): Unit = {
    super.run()

    try {
      val out: BufferedOutputStream = new BufferedOutputStream(clientConnection.getOutputStream())
      val in: BufferedInputStream = new BufferedInputStream(clientConnection.getInputStream())

      val output: ObjectOutputStream = new ObjectOutputStream(out)
      output.write(clientNumber)
      output.flush()
      val input: ObjectInputStream = new ObjectInputStream(in)
      input.read()

      while (!isInterrupted) {
        output.reset()

        // client state
        state = input.readObject().asInstanceOf[STATE.Value]

        val clientPrimeNumbers = input.readObject().asInstanceOf[Int]
        if (server.serverThreads.size == 1 && clientPrimeNumbers == 0)
          output.writeObject(server.primesNumbers)
        else
          output.writeObject(null)
        output.flush()

        if (state == STATE.READY) {
          while (!setNumber) {
            Thread.sleep(1)
          }
          output.writeObject(number)
          output.flush()
          state = STATE.CALCULATING

          ans = -1
          var done = false
          while (ans == -1 && !done) {
            var calculated = input.read()
            if (calculated == 1 || calculated == 0) {
              ans = calculated
            } else {
              if (server.calculated == true) {
                output.write(1)
                output.flush()
                val x = input.read()
                done = true
              } else {
                output.write(0)
                output.flush()
              }
            }
            Thread.sleep(1)
          }

          server.synchronized {
            server.receivedAns += 1
          }
          if (ans == 1) {
            server.isNotPrime = true
          }
          number = null
          setNumber = false

          while (!endSending){
            Thread.sleep(1)
          }
          endSending = false
          if (sendPrime)
            output.writeObject(prime)
          else
            output.writeObject(null)
          output.flush()
          sendPrime = false
          prime = null


          if (clientMissed)
            output.writeObject(arr)
          else
            output.writeObject(null)
          output.flush()

          clientMissed = false
          arr.clear()
        }
      }
    } catch {
      case e: Exception => {
        logger.warn("Client " + clientNumber + " disconnected.")
        server.serverThreads.remove(server.serverThreads.indexOf(this))
        server.missesClients.append(clientNumber)
        server.clientMiss = true
        logger.info("Connected Wrokers: " + server.serverThreads.size)
      }
    }
    clientConnection.close()
  }

  def getClientState: STATE.Value = state
}

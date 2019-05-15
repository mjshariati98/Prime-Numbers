import java.awt.Color
import java.io
import java.io.{File, FileInputStream, FileOutputStream, PrintStream}
import java.net.{ServerSocket, Socket}
import java.util.Scanner

import org.apache.log4j.Logger

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.{BufferedSource, StdIn}

class Server(portNumber: Int) {
  val logger = Logger.getLogger("Server")
  val port = portNumber
  val serverThreads = mutable.ArrayBuffer.empty[ServerThread]
  val clientsPrimeNumbersCounter = mutable.ArrayBuffer.empty[Int] // keep every client has how many prime numbers
  val clientsPrimeNumbersKeeper = mutable.ArrayBuffer.empty[ArrayBuffer[Int]]
  var clientNumbers = 0
  val missesClients = mutable.ArrayBuffer.empty[Int]

  val serverSocket = new ServerSocket(port)

  val map = mutable.Map[Int, Int]()

  // log
  val fos: FileOutputStream = new FileOutputStream(new File("log.txt"))
  val printStream: PrintStream = new PrintStream(fos)

  var lastCalculateNumber: BigInt = 2
  var finalNumberToCalculate: BigInt = 3

  var calculated = false

  val primesNumbers = mutable.ArrayBuffer.empty[BigInt]

  primesNumbers.append(2)


  var receivedAns: Int = 0
  var isNotPrime: Boolean = false
  var clientMiss: Boolean = false
  val mainThread: Thread = new Thread(new Runnable {
    override def run(): Unit = {
      lastCalculateNumber += 1
      while (lastCalculateNumber < finalNumberToCalculate) {
        if (serverThreads.size != 0) {
          calculated = false
          val n = lastCalculateNumber
          receivedAns = 0
          isNotPrime = false

          var c: Int = 0
          val size = serverThreads.size
          while (c < size) {
            serverThreads(c).number = n
            serverThreads(c).setNumber = true
            c += 1
          }

          while (receivedAns != size && !clientMiss && !calculated) {
            if (isNotPrime) calculated = true
            Thread.sleep(1)
          }
          if (!clientMiss) {
            if (!isNotPrime) {
              primesNumbers.append(n)

              val choosedClient: Int = clientChooser()
              serverThreads(choosedClient).sendPrime = true
              serverThreads(choosedClient).prime = n
              clientsPrimeNumbersCounter(choosedClient) = clientsPrimeNumbersCounter(choosedClient) + 1
              clientsPrimeNumbersKeeper(choosedClient).append(primesNumbers.size - 1)

              printStream.println(n)
              println("New Prime: " + n + " Store in client " + serverThreads(choosedClient).clientNumber)
              logger.info("all calculated prime numbers: " + primesNumbers.size)
            }

            serverThreads.foreach(elemnet => {
              elemnet.endSending = true
            })

            if (!clientMiss)
              lastCalculateNumber = BigInt.javaBigInteger2bigInt(lastCalculateNumber.bigInteger.nextProbablePrime())
            else
              clientMiss = true
          } else {
            val temp: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty[Int]
            missesClients.foreach(e => temp.append(e))
            temp.foreach(element => {
              val primeNumbersInClient: Int = clientsPrimeNumbersCounter(map.get(element).get)
              val availableClients = serverThreads.size
              if (availableClients != 0) {
                val partLength = primeNumbersInClient / availableClients


                val clientsPrimeNumbersCounter2 = mutable.ArrayBuffer.empty[Int] // keep every client has how many prime numbers
                val clientsPrimeNumbersKeeper2 = mutable.ArrayBuffer.empty[ArrayBuffer[Int]]

                clientsPrimeNumbersCounter.foreach(e => clientsPrimeNumbersCounter2.append(e))
                clientsPrimeNumbersKeeper.foreach(e => clientsPrimeNumbersKeeper2.append(e))

                clientsPrimeNumbersCounter.remove(map.get(element).get)
                clientsPrimeNumbersKeeper.remove(map.get(element).get)


                for (i <- 0 to clientNumbers) {
                  if (map.get(i) != null && map.get(i) != None && i > element)
                    map.put(i, map.get(i).get - 1)
                }

                var i: Int = 0
                var cl: Int = 0
                var j: Int = 0
                while (i < primeNumbersInClient) {
                  if (cl == availableClients)
                    cl = 0
                  serverThreads(cl).clientMissed = true
                  serverThreads(cl).arr.append(primesNumbers(clientsPrimeNumbersKeeper2(map.get(element).get)(i)))

                  clientsPrimeNumbersCounter(cl) = clientsPrimeNumbersCounter(cl) + 1
                  clientsPrimeNumbersKeeper(cl).append(clientsPrimeNumbersKeeper2(map.get(element).get)(i))
                  i += 1
                  j += 1
                  if (j == partLength) {
                    cl += 1
                    j = 0
                  }
                }
              }
            })

            serverThreads.foreach(elemnet => {
              elemnet.endSending = true
            })

            temp.foreach(e => missesClients.remove(missesClients.indexOf(e)))

            clientMiss = false
          }
        } else {
          clientsPrimeNumbersCounter.clear()
          clientsPrimeNumbersKeeper.clear()
        }
      }
    }
  })

  def clientChooser(): Int = { // choose which client to save the new prime number
    var min: Int = Integer.MAX_VALUE
    var index: Int = -1
    for (i <- 0 to clientsPrimeNumbersCounter.size - 1) {
      if (clientsPrimeNumbersCounter(i) < min) {
        min = clientsPrimeNumbersCounter(i)
        index = i
      }
    }
    index
  }

}

object Server {
  def main(args: Array[String]): Unit = {

    System.out.println("Enter Port: ")
    val port = StdIn.readInt()
    val server: Server = new Server(port)
    server.logger.info("Server program is starting and ready on port 9998...")

    System.err.println("For starting at initial state (Enter 1) or Loading previous Date (Enter 2) :")
    val x = StdIn.readInt()
    if (x == 1) {
      System.err.println("Enter the upper bound of calculation...")
      server.finalNumberToCalculate = BigInt(StdIn.readLine())
      server.logger.info("Start calculating prime numbers in range 2 to " + server.finalNumberToCalculate)
    } else {
      System.err.println("Enter the path of file: ")
      val s = StdIn.readLine()
      val fis: FileInputStream = new FileInputStream(new File(s))
      val scanner: Scanner = new Scanner(fis)
      while (scanner.hasNext()) {
        val xx = scanner.nextInt()
        server.primesNumbers.append(xx)
      }
      server.lastCalculateNumber = server.primesNumbers(server.primesNumbers.size - 1).toInt
      println("Load successfully.")
      System.err.println("Enter the upper bound of calculation...")
      server.finalNumberToCalculate = BigInt(StdIn.readLine())
      server.logger.info("Start calculating prime numbers in range " + server.lastCalculateNumber + " to " + server.finalNumberToCalculate)
    }

    server.mainThread.start()

    while (true) {
      server.logger.info("Waiting for clients ...")
      val clientConnection: Socket = server.serverSocket.accept()
      server.clientNumbers += 1
      val thread: ServerThread = new ServerThread(server, clientConnection, server.clientNumbers, server.logger)
      thread.start()
      server.serverThreads.append(thread)
      server.clientsPrimeNumbersCounter.append(0)
      server.clientsPrimeNumbersKeeper.append(mutable.ArrayBuffer[Int]())
      server.logger.info("Client " + server.clientNumbers + " connected successfully.")
      server.map.put(server.clientNumbers, server.clientsPrimeNumbersCounter.size - 1)
      server.logger.info("Connected Wrokers: " + server.serverThreads.size)
    }
  }
}

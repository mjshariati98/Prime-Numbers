import java.io._
import java.net.Socket
import scala.collection.mutable

class ClientThread(client: Client, connection: Socket) extends Thread with Serializable {
  @transient val socket = connection
  var clientNumber: Int = 0
  var state: STATE.Value = STATE.READY
  var number: BigInt = null
  var calculateDone = false
  var ans = 0


  override def run(): Unit = {
    super.run()
    try {

      val out: BufferedOutputStream = new BufferedOutputStream(socket.getOutputStream())
      val in: BufferedInputStream = new BufferedInputStream(socket.getInputStream())

      val output: ObjectOutputStream = new ObjectOutputStream(out)
      output.write(0)
      output.flush()
      val input: ObjectInputStream = new ObjectInputStream(in)

      clientNumber = input.read()
      println("Client: " + clientNumber)

      while (!socket.isClosed && !isInterrupted) {
        output.writeObject(state)
        output.writeObject(client.primesNumbers.size)
        output.flush()


        val allPrimeNumbers = input.readObject().asInstanceOf[mutable.ArrayBuffer[BigInt]]
        if (allPrimeNumbers != null)
          allPrimeNumbers.foreach(e => client.primesNumbers.append(e))



        if (state == STATE.READY) {
          calculateDone = false
          var otherClientCalculated = 0

          number = input.readObject().asInstanceOf[BigInt]
          state = STATE.CALCULATING
          println("Calculating " + number)

          // calculate
          ans = -1
          val n: Int = Math.sqrt(number.toDouble).toInt
          client.number = number
          client.n = n



          while (otherClientCalculated == 0 && !calculateDone) {
            // 0: end 1:work
            output.write(-1)
            output.flush()
            otherClientCalculated = input.read()
            if (otherClientCalculated == 1) {
              client.stop = true
              client.n = 0
            }
            Thread.sleep(1)
          }
          output.write(ans)
          output.flush()

          val prime = input.readObject().asInstanceOf[BigInt]
          if (prime != null)
            client.primesNumbers.append(prime)


          client.number = 0


          val arr = input.readObject().asInstanceOf[mutable.ArrayBuffer[BigInt]]
          if (arr != null) {
            arr.foreach(e => client.primesNumbers.append(e))
          }

          state = STATE.READY

          println("Prime Numbers keep in this client: " + client.primesNumbers.size)
          client.primesNumbers.foreach(e => print(e + " , "))
          println()
          println("======================")
        }
      }

    } catch {
      case e: Exception => {
        System.err.println("Connection Lost!")
        System.exit(0)
      }
    }
  }
}

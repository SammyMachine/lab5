import java.net.InetAddress
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


fun main(args: Array<String>) {
    run(arrayOf("twitch.tv", "7"))
}

@OptIn(ExperimentalTime::class)
fun run(args: Array<String>) {
    val targetServerAddress =
        args.getOrNull(0) ?: throw IllegalArgumentException("\"Server address\" expected in first arg")
    val numOfPings = args.getOrNull(1) ?: throw IllegalArgumentException("\"Number of pings\" expected in second arg")
    val rtt = mutableListOf<Duration>()
    repeat(numOfPings.toInt()) {
        val (success, duration) = measureTimedValue {
            InetAddress.getByName(targetServerAddress).isReachable(1000)
        }
        if (success) {
            rtt += duration
            println("Echo response received from ${InetAddress.getByName(targetServerAddress).hostAddress} " +
                    "with time ${duration.inWholeMilliseconds}ms")
        } else {
            println("Request timed out")
        }
    }
    val failures = 4 - rtt.size
    println("\nPing stats for ${InetAddress.getByName(targetServerAddress).hostAddress}:")
    println("### Packets ###\nSent = $4, Received = ${4 - failures}, Lost = $failures (${(failures.toDouble() / 4) * 100}% loss)")
    println("### RTT stats ###")
    val minMilliseconds = if (rtt.isEmpty()) 0 else rtt.min().inWholeMilliseconds
    val maxMilliseconds = if (rtt.isEmpty()) 0 else rtt.max().inWholeMilliseconds
    val averageMilliseconds = if (rtt.isEmpty()) 0 else rtt.sumOf { it.inWholeMilliseconds } / rtt.size
    println("Min time ${minMilliseconds}ms\nMax time ${maxMilliseconds}ms\nAvg time ${averageMilliseconds}ms")
}

package cinema.model

import cinema.dto.SeatDTO
import cinema.dto.SeatResponseDTO
import org.springframework.stereotype.Component

const val FRONT_PRICE = 10
const val BACK_PRICE = 8
const val SMALL_ROOM_SIZE = 60


// Class that represents a distinct room in a cinema
@Component
class Room(val rows: Int = 9, val columns: Int = 9) {
    // 2d list of all the seats in the cinema room
    val roomSeats: List<MutableList<Char>> = List(rows) { MutableList(columns) { 'S' } }
    val small = rows * columns <= SMALL_ROOM_SIZE
    val purchased: Int get() = countPurchasedSeats(roomSeats)
    val capacity: Double get() = purchased.toDouble() / (rows * columns) * 100
    val frontRows get() = roomSeats.take(rows.div(2))
    val backRows get() = roomSeats.takeLast(rows.divR(2))

    fun getDTO(): SeatResponseDTO {
        val seatList = roomSeats.mapIndexed { y, row ->
            List(row.size) { x ->
                SeatDTO(y + 1, x + 1, getSeatPrice(y))
            }
        }.flatten()
        return SeatResponseDTO(rows, columns, seatList)
    }

    fun print() {
        println("Cinema:")
        print("  ")
        println((1..roomSeats[0].size).joinToString(" "))
        roomSeats.forEachIndexed { i, list ->
            println((i + 1).toString() + " " + list.joinToString(" "))
        }
    }

    fun get(row: Int, seat: Int): Char {
        return roomSeats[row - 1][seat - 1]
    }

    // calculate total income based on rows and total seat count
    fun getTotalIncome(): Int {
        return if (small) rows * columns * FRONT_PRICE else {
            rows.div(2) * columns * FRONT_PRICE +
                    rows.divR(2) * columns * BACK_PRICE
        }
    }

    fun getSeatPrice(row: Int): Int {
        return if (row < 4) 10 else 8
//        return if (small || row <= this.rows / 2) FRONT_PRICE else BACK_PRICE
    }

    fun bookSeat(row: Int, column: Int): Int? {
        val ch = roomSeats[row - 1][column - 1]
        if (ch == 'B') {
            return null
        } else {
            roomSeats[row - 1][column - 1] = 'B'
            return getSeatPrice(row)
        }
    }

    fun getCurrentIncome(): Int {
        return if (small) purchased * FRONT_PRICE else {
            countPurchasedSeats(frontRows) * FRONT_PRICE + countPurchasedSeats(backRows) * BACK_PRICE
        }
    }

    private fun countPurchasedSeats(rows: List<List<Char>>): Int = rows.flatten().count { it == 'B' }
}
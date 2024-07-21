package cinema.model

import cinema.dto.SeatDTO
import java.util.*

const val FRONT_PRICE = 10
const val BACK_PRICE = 8
const val SMALL_ROOM_SIZE = 60


// Class that represents a distinct room in a cinema

class Room(val rows: Int = 9, val columns: Int = 9) {
    val tickets = emptyMap<UUID, SeatDTO>().toMutableMap()

    // 2d list of all the seats in the cinema room
    val roomSeats: List<MutableList<Char>> = List(rows) { MutableList(columns) { 'S' } }
    val small = rows * columns <= SMALL_ROOM_SIZE
    val purchased: Int get() = countPurchasedSeats(roomSeats)
    val capacity: Double get() = purchased.toDouble() / (rows * columns) * 100
    val frontRows get() = roomSeats.take(rows.div(2))
    val backRows get() = roomSeats.takeLast(rows.divR(2))

    fun getSeatList(): List<SeatDTO> {
        val seatList = roomSeats.mapIndexed { y, row ->
            List(row.size) { x ->
                SeatDTO(y + 1, x + 1, getSeatPrice(y))
            }
        }.flatten()
        return seatList
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

    fun get(token: UUID): SeatDTO? {
        return tickets[token]
    }

    fun unbook(token: UUID): SeatDTO? {
        val seatDTO = tickets.remove(token)
        if (seatDTO == null) {
            return null
        }
        roomSeats[seatDTO.row - 1][seatDTO.column - 1] = 'S'
        return seatDTO
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

    fun bookSeat(row: Int, column: Int): UUID {
        roomSeats[row - 1][column - 1] = 'B'
        val seat = SeatDTO(row, column, getSeatPrice(row))
        val token = UUID.randomUUID()
        tickets[token] = seat
        return token

    }

    fun getCurrentIncome(): Int {
        return if (small) purchased * FRONT_PRICE else {
            countPurchasedSeats(frontRows) * FRONT_PRICE + countPurchasedSeats(backRows) * BACK_PRICE
        }
    }

    private fun countPurchasedSeats(rows: List<List<Char>>): Int = rows.flatten().count { it == 'B' }

    class Seat(val x: Int, val y: Int)
}
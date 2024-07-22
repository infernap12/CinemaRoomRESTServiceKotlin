package cinema.model

import cinema.SeatBookException
import cinema.dto.PurchaseResponseDTO
import cinema.dto.ReturnRequestDTO
import cinema.dto.SeatDTO
import cinema.dto.SeatsResponseDTO
import cinema.dto.StatsResponseDTO
import org.springframework.stereotype.Component

@Component
class CinemaService {
    val rooms = emptyList<Room>().toMutableList()

    init {
        newRoom(9, 9)
    }

    fun newRoom(rows: Int, columns: Int) {
        rooms.add(Room(rows, columns))
    }

    fun purchaseSeat(requestedSeat: SeatDTO): PurchaseResponseDTO? {
        val room = rooms[0]
        if (requestedSeat.row !in 1..room.rows || requestedSeat.column !in 1..room.columns) {
            throw SeatBookException("The number of a row or a column is out of bounds!")
        }
        val ch = room.roomSeats[requestedSeat.row - 1][requestedSeat.column - 1]
        val price = room.getSeatPrice(requestedSeat.row)
        if (ch == 'B') {
            return null
        } else {
            val token = room.bookSeat(requestedSeat.row, requestedSeat.column)
            return PurchaseResponseDTO(token, SeatDTO(requestedSeat.row, requestedSeat.column, price))
        }
    }

    fun getRoomDTO(roomID: Int = 0): SeatsResponseDTO {
        val room = rooms[roomID]
        return SeatsResponseDTO(totalRows = room.rows, room.columns, room.getSeatList())
    }

    fun returnSeat(dTO: ReturnRequestDTO, roomID: Int = 0): SeatDTO {
        val room = rooms[roomID]
        val unbookedSeat = room.unbook(dTO.token)
        if (unbookedSeat == null) {
            throw SeatBookException("Wrong token!")
        }
        return unbookedSeat
    }

    fun getStats(roomID: Int = 0): StatsResponseDTO {
        val room = rooms[roomID]
        val currentIncome = room.getCurrentIncome()
        val remainingSeats = room.available
        val purchasedTickets = room.purchased
        return StatsResponseDTO(currentIncome, remainingSeats, purchasedTickets)
    }
}
package cinema

import cinema.dto.SeatDTO
import cinema.model.Room
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
class CinemaRestController(private val room: Room) {

    @GetMapping(path = ["/seats"])
    fun seats() = ResponseEntity(room.getDTO(), HttpStatus.OK)

    @PostMapping(path = ["/purchase"])
    fun purchases(@RequestBody() seat: SeatDTO): ResponseEntity<SeatDTO> {
        if (seat.row !in 1..room.rows || seat.column !in 1..room.columns) {
            throw SeatBookException("The number of a row or a column is out of bounds!")
        }
        val price = room.bookSeat(row = seat.row, column = seat.column)
            ?: throw SeatBookException("The ticket has been already purchased!")

        return ResponseEntity(SeatDTO(seat.row, seat.column, price), HttpStatus.OK)

    }

    @ExceptionHandler(SeatBookException::class)
    fun handleSeatAlreadyBooked(
        e: SeatBookException, request: WebRequest
    ): ResponseEntity<CustomErrorMessage> {

        val body = e.message?.let {
            CustomErrorMessage(
                it
            )
        }
        return ResponseEntity<CustomErrorMessage>(body, HttpStatus.BAD_REQUEST)
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class SeatBookException(message: String) : RuntimeException(message)

class CustomErrorMessage(val error: String)
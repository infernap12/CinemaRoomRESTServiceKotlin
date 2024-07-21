package cinema

import cinema.dto.*
import cinema.model.CinemaService
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
class CinemaRestController(private val cinemaService: CinemaService) {
    @GetMapping(path = ["/seats"])
    fun seats() = ResponseEntity(cinemaService.getRoomDTO(), HttpStatus.OK)

    @PostMapping(path = ["/purchase"])
    fun purchases(@RequestBody seat: SeatDTO): ResponseEntity<PurchaseResponseDTO> {
        val deeto = cinemaService.purchaseSeat(seat)
        if (deeto == null) {
            throw SeatBookException("The ticket has been already purchased!")
        }
        return ResponseEntity(deeto, HttpStatus.OK)
    }


    @PostMapping(path = ["/return"])
    fun returns(@RequestBody req: ReturnRequestDTO): ReturnResponseDTO {
        val returnDto = ReturnResponseDTO(cinemaService.returnSeat(req))
        println(returnDto.toString())
        return returnDto
    }

    @ExceptionHandler(SeatBookException::class)
    fun handleSeatAlreadyBooked(e: SeatBookException, request: WebRequest): ResponseEntity<CustomErrorMessage> {
        val body = e.message?.let { CustomErrorMessage(it) }
        return ResponseEntity<CustomErrorMessage>(body, HttpStatus.BAD_REQUEST)
    }
}


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class SeatBookException(message: String) : RuntimeException(message)

class CustomErrorMessage(val error: String)
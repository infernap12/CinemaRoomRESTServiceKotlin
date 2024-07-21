package cinema.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ReturnResponseDTO(
    @JsonProperty("returned_ticket", required = true) val seatDTO: SeatDTO
)

package cinema.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

class PurchaseResponseDTO(
    val token: UUID,
    @JsonProperty("ticket") val seat: SeatDTO
)
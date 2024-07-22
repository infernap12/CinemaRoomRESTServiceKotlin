package cinema.dto

import com.fasterxml.jackson.annotation.JsonProperty

class StatsResponseDTO(
    @JsonProperty("current_income") val income: Int,
    @JsonProperty("number_of_available_seats") val availableSeats: Int,
    @JsonProperty("number_of_purchased_tickets") val purchasedTickets: Int
)
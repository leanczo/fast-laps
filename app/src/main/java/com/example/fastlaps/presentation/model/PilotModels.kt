data class DriverStandingsResponse(
    val MRData: MRData
)

data class MRData(
    val StandingsTable: StandingsTable
)

data class StandingsTable(
    val StandingsLists: List<StandingsList>
)

data class StandingsList(
    val DriverStandings: List<DriverStanding>
)

data class DriverStanding(
    val position: String= "",
    val positionText: String= "",
    val points: String= "",
    val wins: String= "",
    val Driver: Driver= Driver(),
    val Constructors: List<Constructor> = emptyList()
)

data class Driver(
    val driverId: String= "",
    val permanentNumber: String= "",
    val code: String= "",
    val givenName: String= "",
    val familyName: String= "",
    val nationality: String= ""
)



data class Constructor(
    val name: String,
    val nationality: String
)

data class DriverStandingsResponse(
    val MRData: DriverMRData
)

data class DriverMRData(
    val StandingsTable: DriverStandingsTable
)

data class DriverStandingsTable(
    val StandingsLists: List<DriverStandingsList>
)

data class DriverStandingsList(
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




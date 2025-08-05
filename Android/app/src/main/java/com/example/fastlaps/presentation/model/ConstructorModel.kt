data class ConstructorStandingsResponse(
    val MRData: ConstructorMRData
)

data class ConstructorMRData(
    val StandingsTable: ConstructorStandingsTable
)

data class ConstructorStandingsTable(
    val StandingsLists: List<ConstructorStandingsList> = emptyList()
)

data class ConstructorStandingsList(
    val ConstructorStandings: List<ConstructorStanding> = emptyList()
)
data class ConstructorStanding(
    val position: String ="",
    val points: String ="",
    val wins: String ="",
    val Constructor: Constructor = Constructor()
)

data class Constructor(
    val constructorId: String ="",
    val name: String ="",
    val nationality: String =""
)
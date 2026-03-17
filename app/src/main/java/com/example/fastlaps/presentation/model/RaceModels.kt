data class RaceTableResponse(
    val MRData: RaceMRData = RaceMRData()
)

data class RaceMRData(
    val RaceTable: RaceTable = RaceTable()
)

data class RaceTable(
    val season: String = "",
    val Races: List<Race> = emptyList()
)

data class Race(
    val season: String = "",
    val round: String = "",
    val raceName: String = "",
    val Circuit: RaceCircuit = RaceCircuit(),
    val date: String = "",
    val time: String = "",
    val FirstPractice: SessionTime? = null,
    val SecondPractice: SessionTime? = null,
    val ThirdPractice: SessionTime? = null,
    val Qualifying: SessionTime? = null,
    val Sprint: SessionTime? = null,
    val Results: List<RaceResult> = emptyList(),
    val QualifyingResults: List<QualifyingResult> = emptyList(),
    val SprintResults: List<RaceResult> = emptyList()
)

data class SessionTime(
    val date: String = "",
    val time: String = ""
)

data class RaceCircuit(
    val circuitId: String = "",
    val circuitName: String = "",
    val Location: CircuitLocation = CircuitLocation()
)

data class CircuitLocation(
    val locality: String = "",
    val country: String = ""
)

data class RaceResult(
    val number: String = "",
    val position: String = "",
    val positionText: String = "",
    val points: String = "",
    val Driver: Driver = Driver(),
    val Constructor: Constructor = Constructor(),
    val grid: String = "",
    val laps: String = "",
    val status: String = "",
    val Time: RaceTime? = null,
    val FastestLap: FastestLap? = null
)

data class RaceTime(
    val millis: String = "",
    val time: String = ""
)

data class FastestLap(
    val rank: String = "",
    val lap: String = "",
    val Time: LapTime? = null
)

data class LapTime(
    val time: String = ""
)

data class QualifyingResult(
    val number: String = "",
    val position: String = "",
    val Driver: Driver = Driver(),
    val Constructor: Constructor = Constructor(),
    val Q1: String = "",
    val Q2: String = "",
    val Q3: String = ""
)

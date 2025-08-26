import { FLPageHeader } from '../components/FLPageHeader'
import { LengthMetrics } from '@kit.ArkUI'
import FLTheme from '../FLTheme'
import { FLRoundButton } from '../components/FLRoundButton'


const DummyData: string[] = [
  "Monte Carlo, Monaco1",
  "Monte Carlo, Monaco2",
  "Monte Carlo, Monaco3",
  "Monte Carlo, Monaco4"
]

@Builder
export function RacesScreenBuilder(name: string, param: Object) {
  RacesScreen()
}

@Entry
@Component
struct RacesScreen {
  @State RacesData: string[] = []
  @State isLoading: boolean = true

  aboutToAppear(): void {
    this.onRefresh()
  }

  build() {
    Stack() {
      if (this.isLoading) {
        Stack() {
          LoadingProgress()
            .height('20%')
            .width('20%')
        }
        .width("100%")
        .height('100%')
        .backgroundColor(FLTheme.COLOR_BG_BACKDROP)
        .zIndex(2)
        .onClick(e => true)
      }

      this.content()

    }.width("100%")
    .height('100%')

  }

  @Builder
  content() {
    Scroll() {

      Column() {

        Blank().height('5%')

        FLPageHeader({
          title: "Races",
          type: 'secondary'
        })

        Blank().height('5%')

        FLRoundButton({
          text: "↺",
          buttonSize: '48vp'
        })
          .onClick(e => this.onRefresh())

        Blank().height('5%')

        ForEach(this.RacesData, (raceName: string) => {

          this.renderRace(raceName)

          Blank().height("5%")

        })

        Blank()
          .height("20%")


      } //column
      .constraintSize({ minHeight: '100%' })
      .width("100%")
      .padding({
        start: LengthMetrics.vp(20),
        end: LengthMetrics.vp(20)
      })

    } //Scroll
    .width("100%")
    .height("100%")
    .focusable(true)
    .defaultFocus(true) //to scroll with physical rotating button (Crown button)
    .backgroundColor(FLTheme.COLOR_BG)
    .edgeEffect(EdgeEffect.Spring)
    .scrollBar(BarState.Off)

  }

  @Builder
  renderRace(name: string) {

    Row() {
      Text(name)
        .fontWeight(FontWeight.Medium)
        .fontSize("15fp")
        .fontColor(FLTheme.COLOR_TEXT_ON_SURFACE)
    }
    .width("100%")
    .height(FLTheme.ButtonHeight)
    .align(Alignment.Start)
    .borderRadius('100%')
    .padding({
      start: LengthMetrics.vp(10),
      end: LengthMetrics.vp(10)
    })
    .backgroundColor(FLTheme.COLOR_BG_SURFACE)

  }

  onRefresh() {
    console.log('onRefresh')
    this.isLoading = true
    this.RacesData = []
    setTimeout(() => {
      this.RacesData = DummyData
      this.isLoading = false
    }, 1000)
  }
}
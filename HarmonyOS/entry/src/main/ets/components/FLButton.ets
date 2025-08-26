import FLTheme from "../FLTheme"
import { LengthMetrics } from "@kit.ArkUI"

@Component
export struct FLButton {
  @Prop title: string
  @Prop icon?: Resource

  build() {

    Button() {
      Row() {
        if (this.icon) {
          Image(this.icon)
            .width('15%')
            .height('100%')
            .objectFit(ImageFit.Contain)
        }

        Blank().width('5%')

        Text(this.title)
          .fontWeight(FontWeight.Bold)
          .fontSize("20fp")
          .fontColor(FLTheme.COLOR_TEXT_PRIMARY)
      }
      .width("100%")
      .align(Alignment.Start)
      .padding({ start: LengthMetrics.vp(20), end: LengthMetrics.vp(20) })
    }
    .width('100%')
    .height(FLTheme.ButtonHeight)
    .type(ButtonType.Capsule)
    .backgroundColor(FLTheme. COLOR_PRIMARY)
    .clickEffect({ level: ClickEffectLevel.MIDDLE })


  }
}
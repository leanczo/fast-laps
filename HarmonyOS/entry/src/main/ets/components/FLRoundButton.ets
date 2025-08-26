import FLTheme from "../FLTheme"
import { LengthMetrics } from "@kit.ArkUI"

@Component
export struct FLRoundButton {
  @Prop text: string
  @Prop buttonSize: Length = FLTheme.ButtonHeight

  build() {

    Button(this.text)
      .width(this.buttonSize)
      .height(this.buttonSize)
      .fontWeight(FontWeight.Bold)
      //
      .type(ButtonType.Capsule)
      .backgroundColor(FLTheme.COLOR_PRIMARY)
      .fontColor(FLTheme.COLOR_TEXT_PRIMARY)
      .clickEffect({ level: ClickEffectLevel.MIDDLE })

  }
}
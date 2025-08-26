import FLTheme from "../FLTheme"

export type PageHeaderType = 'primary' | 'secondary'

@Component
export struct FLPageHeader {
  @Prop title: string
  @Prop icon?: Resource
  @Prop type: PageHeaderType = 'primary'

  isPrimary() {
    return this.type === 'primary'
  }

  build() {
    Row() {
      if (this.icon) {
        Image(this.icon)
          .width('10%')
          .height('10%')
          .objectFit(ImageFit.Contain)
      }

      Blank().width(5)

      Text("FastLaps")
        .fontWeight(this.isPrimary() ? FontWeight.Bold : FontWeight.Medium)
        .fontStyle(this.isPrimary() ? FontStyle.Italic : FontStyle.Normal)
        .fontColor(this.isPrimary() ? FLTheme.COLOR_PRIMARY : FLTheme.COLOR_TEXT_ON_SURFACE)
        .fontSize("18fp")
    }
    .width('100%')
    .justifyContent(FlexAlign.Center)
  }
}
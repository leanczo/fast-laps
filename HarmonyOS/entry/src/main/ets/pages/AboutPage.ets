import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;

@Builder
export function AboutPageBuilder(name: string, param: Object) {
  AboutPage()
}

@Component
struct AboutPage {
  @State currentLang: string = 'en';
  private params?: Object;

  aboutToAppear() {
    // Get language from navigation params
    try {
      if (this.params && (this.params as string)) {
        this.currentLang = (this.params as string);
      }
    } catch (error) {
      this.currentLang = 'en';
    }
    hilog.info(DOMAIN, 'FastLaps', 'AboutPage initialized with language: %{public}s', this.currentLang);
  }

  build() {
    NavDestination() {
      Column() {
        Column() {
          Text('ℹ️')
            .fontSize(28)
            .margin({ bottom: 8 })

          Text(this.currentLang === 'es' ? 'Acerca de' : 'About')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .fontColor('#FF6B35')
        }
        .width('100%')
        .alignItems(HorizontalAlign.Center)
        .padding({ top: 20, bottom: 20 })

        // Main content in scrollable area
        Scroll() {
          Column({ space: 16 }) {
            // Data sources section
            Column({ space: 8 }) {
              Text(this.currentLang === 'es' ? 'Datos proporcionados por:' : 'Data provided by:')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
                .fontColor('#FFFFFF')
                .textAlign(TextAlign.Center)

              Column({ space: 4 }) {
                Text('OpenF1 & Ergast API')
                  .fontSize(14)
                  .fontColor('#FF6B35')
                  .textAlign(TextAlign.Center)

                Text(this.currentLang === 'es' 
                  ? 'APIs no oficiales de F1 con datos históricos y en tiempo real'
                  : 'Unofficial F1 APIs with historical and real-time data')
                  .fontSize(12)
                  .fontColor('#CCCCCC')
                  .textAlign(TextAlign.Center)
                  .margin({ top: 4 })
              }
              .width('100%')
            }
            .width('100%')
            .padding(16)

            // Divider
            Divider()
              .color('#333333')
              .strokeWidth(1)
              .margin(20)

            // News sources section
            Column({ space: 8 }) {
              Text(this.currentLang === 'es' ? 'Fuentes de noticias:' : 'News sources:')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
                .fontColor('#FFFFFF')
                .textAlign(TextAlign.Center)

              Column({ space: 6 }) {
                // English news
                Row() {
                  Text(this.currentLang === 'es' ? '🇬🇧 Inglés: ' : '🇬🇧 English: ')
                    .fontSize(12)
                    .fontColor('#CCCCCC')
                  Text('Motorsport.com')
                    .fontSize(12)
                    .fontColor('#FF6B35')
                }
                .justifyContent(FlexAlign.Center)

                // Spanish news  
                Row() {
                  Text(this.currentLang === 'es' ? '🇪🇸 Español: ' : '🇪🇸 Spanish: ')
                    .fontSize(12)
                    .fontColor('#CCCCCC')
                  Text('Motorsport Latam')
                    .fontSize(12)
                    .fontColor('#FF6B35')
                }
                .justifyContent(FlexAlign.Center)
              }
            }
            .width('100%')
            .padding(16)

            // Divider
            Divider()
              .color('#333333')
              .strokeWidth(1)
              .margin(20)

            // Disclaimers section
            Column({ space: 12 }) {
              Text(this.currentLang === 'es'
                ? 'Esta app utiliza APIs públicas y fuentes de noticias para información de F1. Los datos pueden no actualizarse en tiempo real durante las sesiones.'
                : 'This app uses public APIs and news sources for F1 information. Data may not update in real-time during sessions.')
                .fontSize(11)
                .fontColor('#CCCCCC')
                .textAlign(TextAlign.Center)
                .lineHeight(16)

              Text(this.currentLang === 'es'
                ? '⚠️ No afiliada oficialmente con F1, FIA o equipos'
                : '⚠️ Not officially affiliated with F1, FIA or teams')
                .fontSize(11)
                .fontColor('#FF4444')
                .textAlign(TextAlign.Center)
                .fontWeight(FontWeight.Medium)
            }
            .width('100%')
            .padding(20)

            // Footer info
            Column({ space: 4 }) {
              Text('FastLaps v1.63')
                .fontSize(10)
                .fontColor('#666666')
                .textAlign(TextAlign.Center)

              Text('© 2024 - Unofficial F1 App')
                .fontSize(10)
                .fontColor('#666666')
                .textAlign(TextAlign.Center)
            }
            .width('100%')
            .margin({ top: 8, bottom: 20 })
          }
          .width('100%')
        }
        .layoutWeight(1)
        .width('100%')
      }
      .width('100%')
      .height('100%')
      .backgroundColor('#000000')
    }
    .title(this.currentLang === 'es' ? 'Acerca de' : 'About')
    .backgroundColor('#000000')
  }
}
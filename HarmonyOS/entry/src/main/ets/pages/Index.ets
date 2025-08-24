import { preferences } from '@kit.ArkData';
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;

interface MenuButton {
  title: string;
  icon: string;
  route: string;
  color: string;
}

interface NavigationParams {
  lang: string;
}

@Entry
@Component
struct Index {
  @State currentLang: string = 'en';
  @State menuButtons: MenuButton[] = [];
  private navPathStack: NavPathStack = new NavPathStack();

  aboutToAppear() {
    this.initializeLanguage();
    this.setupMenuButtons();
  }

  initializeLanguage() {
    try {
      // Default to 'en' since configPolicy is not available
      this.currentLang = 'en';
      hilog.info(DOMAIN, 'FastLaps', 'Language initialized to: %{public}s', this.currentLang);
    } catch (error) {
      this.currentLang = 'en';
      hilog.error(DOMAIN, 'FastLaps', 'Error initializing language, defaulting to EN');
    }
  }

  setupMenuButtons() {
    if (this.currentLang === 'es') {
      this.menuButtons = [
        { title: 'Carreras', icon: '📍', route: 'RacesPage', color: '#FF6B35' },
        { title: 'Pilotos', icon: '🏆', route: 'DriversPage', color: '#FF6B35' },
        { title: 'Equipos', icon: '👥', route: 'TeamsPage', color: '#FF6B35' },
        { title: 'Noticias', icon: '📰', route: 'NewsPage', color: '#FF6B35' }
      ];
    } else {
      this.menuButtons = [
        { title: 'Races', icon: '📍', route: 'RacesPage', color: '#FF6B35' },
        { title: 'Drivers', icon: '🏆', route: 'DriversPage', color: '#FF6B35' },
        { title: 'Teams', icon: '👥', route: 'TeamsPage', color: '#FF6B35' },
        { title: 'News', icon: '📰', route: 'NewsPage', color: '#FF6B35' }
      ];
    }
  }

  navigateToPage(route: string) {
    try {
      hilog.info(DOMAIN, 'FastLaps', 'Navigating to: %{public}s', route);
      const params: NavigationParams = { lang: this.currentLang };
      this.navPathStack.pushPathByName(route, params);
    } catch (error) {
      hilog.error(DOMAIN, 'FastLaps', 'Navigation error: %{public}s', JSON.stringify(error));
    }
  }

  toggleLanguage() {
    try {
      this.currentLang = this.currentLang === 'en' ? 'es' : 'en';
      this.setupMenuButtons(); // Refresh menu with new language
      hilog.info(DOMAIN, 'FastLaps', 'Language changed to: %{public}s', this.currentLang);
    } catch (error) {
      hilog.error(DOMAIN, 'FastLaps', 'Error changing language: %{public}s', JSON.stringify(error));
    }
  }

  getLanguageButtonText(): string {
    const changeToText = this.currentLang === 'es' ? 'Cambiar a' : 'Change to';
    const targetLang = this.currentLang === 'es' ? 'Inglés' : 'Spanish';
    return `${changeToText} ${targetLang}`;
  }

  build() {
    Navigation(this.navPathStack) {
      Column() {
        // Scrollable menu that occupies full height
        Scroll() {
          Column({ space: 6 }) {
            // Header with logo and title at top of scroll
            Row() {
              Text('🏎️')
                .fontSize(24)
                .margin({ right: 8 })

              Text('FastLaps')
                .fontSize(20)
                .fontWeight(FontWeight.Bold)
                .fontColor('#FF6B35')
            }
            .width('100%')
            .height(60)
            .justifyContent(FlexAlign.Center)
            .alignItems(VerticalAlign.Center)
            .margin({ bottom: 16, top: 16 })

            // Main menu buttons
            ForEach(this.menuButtons, (button: MenuButton, index: number) => {
              Button() {
                Row() {
                  Text(button.icon)
                    .fontSize(20)
                    .margin({ right: 12 })

                  Text(button.title)
                    .fontSize(16)
                    .fontColor('#FFFFFF')
                    .flexGrow(1)
                }
                .width('100%')
                .height('100%')
                .justifyContent(FlexAlign.Start)
                .alignItems(VerticalAlign.Center)
                .padding({ left: 16, right: 16 })
              }
              .width('100%')
              .height(48)
              .backgroundColor(button.color)
              .borderRadius(12)
              .onClick(() => {
                this.navigateToPage(button.route);
              })
            }, (button: MenuButton, index: number) => index.toString())

            // Language toggle button
            Button() {
              Row() {
                Text('🔄')
                  .fontSize(20)
                  .margin({ right: 12 })

                Text(this.getLanguageButtonText())
                  .fontSize(16)
                  .fontColor('#FFFFFF')
                  .flexGrow(1)
              }
              .width('100%')
              .height('100%')
              .justifyContent(FlexAlign.Start)
              .alignItems(VerticalAlign.Center)
              .padding({ left: 16, right: 16 })
            }
            .width('100%')
            .height(56)
            .backgroundColor('#444444')
            .borderRadius(12)
            .onClick(() => {
              this.toggleLanguage();
            })

            // About button
            Button() {
              Row() {
                Text('ℹ️')
                  .fontSize(20)
                  .margin({ right: 12 })

                Text(this.currentLang === 'es' ? 'Acerca de' : 'About')
                  .fontSize(16)
                  .fontColor('#FFFFFF')
                  .flexGrow(1)
              }
              .width('100%')
              .height('100%')
              .justifyContent(FlexAlign.Start)
              .alignItems(VerticalAlign.Center)
              .padding({ left: 16, right: 16 })
            }
            .width('100%')
            .height(56)
            .backgroundColor('#444444')
            .borderRadius(12)
            .onClick(() => {
              this.navigateToPage('AboutPage');
            })

            // Test button for debugging
            Button() {
              Row() {
                Text('🧪')
                  .fontSize(20)
                  .margin({ right: 12 })

                Text('TEST NAVIGATION')
                  .fontSize(16)
                  .fontColor('#FFFFFF')
                  .flexGrow(1)
              }
              .width('100%')
              .height('100%')
              .justifyContent(FlexAlign.Start)
              .alignItems(VerticalAlign.Center)
              .padding({ left: 16, right: 16 })
            }
            .width('100%')
            .height(56)
            .backgroundColor('#00FF00')
            .borderRadius(12)
            .onClick(() => {
              hilog.info(DOMAIN, 'FastLaps', 'Test button clicked! Language: %{public}s', this.currentLang);
              this.navigateToPage('AboutPage');
            })

            // Footer inside scroll
            Column() {
              Text('v1.63')
                .fontSize(12)
                .fontColor('#999999')
                .opacity(0.6)
                .margin({ bottom: 4 })

              Text('© 2024 FastLaps')
                .fontSize(12)
                .fontColor('#999999')
                .opacity(0.6)
            }
            .width('100%')
            .alignItems(HorizontalAlign.Center)
            .padding({ top: 16, bottom: 8 })
          }
          .width('100%')
          .alignItems(HorizontalAlign.Center)
          .padding({
            left: 16,
            right: 16,
            top: 0,
            bottom: 0
          })
          .justifyContent(FlexAlign.Start)
          .backgroundColor('#000000')
        }
        .scrollable(ScrollDirection.Vertical)
        .scrollBar(BarState.Auto)
        .edgeEffect(EdgeEffect.Spring)
        .width('100%')
        .height('100%') // Asegura que el Scroll ocupe toda la altura
      }
      .width('100%')
      .height('100%') // Asegura que el Column principal ocupe toda la altura
      .backgroundColor('#000000')
    }
    .width('100%')
    .height('100%') // Asegura que Navigation ocupe toda la pantalla
  }
}
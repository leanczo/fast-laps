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
      // Header with logo and title
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
      .margin({ bottom: 16 })

      // Scrollable menu
      Scroll() {
        Column() {
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
            .height(56)
            .backgroundColor(button.color)
            .borderRadius(12)
            .margin({ bottom: 12, left: 16, right: 16 })
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
          .margin({ bottom: 12, left: 16, right: 16 })
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
          .margin({ bottom: 12, left: 16, right: 16 })
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
          .margin({ bottom: 12, left: 16, right: 16 })
          .onClick(() => {
            hilog.info(DOMAIN, 'FastLaps', 'Test button clicked! Language: %{public}s', this.currentLang);
            this.navigateToPage('AboutPage');
          })
        }
        .width('100%')
      }
      .flexGrow(1)
      .width('100%')

      // Footer
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
      .padding({ top: 8, bottom: 16, left: 16, right: 16 })
      }
      .width('100%')
      .height('100%')
      .backgroundColor('#000000')
      .padding(16)
    }
  }
}
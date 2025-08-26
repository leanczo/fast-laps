import { preferences } from '@kit.ArkData';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { LengthMetrics, router } from '@kit.ArkUI';
import { FLButton } from '../components/FLButton';
import { FLPageHeader } from '../components/FLPageHeader';

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

export default class FLTheme {
  static COLOR_PRIMARY = "#ecd389"
  static COLOR_BG = "#000000"
  static COLOR_TEXT_PRIMARY = "#323232"
  static COLOR_BG_SURFACE = "#434343"
  static COLOR_TEXT_ON_SURFACE = "#ffffff"
  static ButtonHeight = '60vp'
  static COLOR_BG_BACKDROP = "#bf000000"
}

interface MainButton {
  title: string
  icon: Resource
  action: () => void
}

@Entry
@Component
struct Index {
  @State currentLang: string = 'en';
  @State menuButtons: MenuButton[] = [];
  private navPathStack: NavPathStack = new NavPathStack();
  @State navPath: NavPathStack = new NavPathStack();
  
  @State buttons: MainButton[] = [{
    title: 'Races',
    icon: $r('app.media.startIcon'),
    action: () => this.onRacesButtonClicked()
  }, {
    title: 'Drivers',
    icon: $r('app.media.startIcon'),
    action: () => this.onDriversButtonClicked()
  }, {
    title: 'Teams',
    icon: $r('app.media.startIcon'),
    action: () => this.onTeamsButtonClicked()
  }, {
    title: 'News',
    icon: $r('app.media.startIcon'),
    action: () => this.onNewsButtonClicked()
  }];

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
    this.buttons = [{
      title: this.currentLang === 'es' ? 'Carreras' : 'Races',
      icon: $r('app.media.startIcon'),
      action: () => this.onRacesButtonClicked()
    }, {
      title: this.currentLang === 'es' ? 'Pilotos' : 'Drivers',
      icon: $r('app.media.startIcon'),
      action: () => this.onDriversButtonClicked()
    }, {
      title: this.currentLang === 'es' ? 'Equipos' : 'Teams',
      icon: $r('app.media.startIcon'),
      action: () => this.onTeamsButtonClicked()
    }, {
      title: this.currentLang === 'es' ? 'Noticias' : 'News',
      icon: $r('app.media.startIcon'),
      action: () => this.onNewsButtonClicked()
    }];
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
      this.setupMenuButtons();
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
    Scroll() {
      Column() {
        Blank().height('10%')

        FLPageHeader({
          title: "FastLaps",
          icon: $r("app.media.startIcon"),
          type: 'primary'
        })

        Blank().height('10%')

        ForEach(this.buttons, (button: MainButton) => {
          FLButton({
            title: button.title,
            icon: button.icon
          }).onClick(e => button.action())

          Blank().height("5%")
        })

        // Language toggle button
        FLButton({
          title: this.getLanguageButtonText(),
          icon: $r('app.media.startIcon')
        }).onClick(() => {
          this.toggleLanguage();
        })

        Blank().height("5%")

        // About button
        FLButton({
          title: this.currentLang === 'es' ? 'Acerca de' : 'About',
          icon: $r('app.media.startIcon')
        }).onClick(() => {
          this.onAboutButtonClicked();
        })

        Blank().height("20%")

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
      }
      .width("100%")
      .padding({
        start: LengthMetrics.vp(20),
        end: LengthMetrics.vp(20)
      })
      .backgroundColor('#000000')
    }
    .scrollable(ScrollDirection.Vertical)
    .scrollBar(BarState.Auto)
    .edgeEffect(EdgeEffect.Spring)
    .backgroundColor('#000000')
    .width('100%')
    .height('100%')
  }

  /*** actions ***/
  onRacesButtonClicked(): void {
    try {
      const uiContext = this.getUIContext();
      const router = uiContext.getRouter();
      router.pushUrl({
        url: '@bundle:com.leandro.fastLaps/entry/ets/pages/RacesScreen'
      });
      hilog.info(DOMAIN, 'FastLaps', 'Navigating to RacesScreen using full bundle path');
    } catch (error) {
      hilog.error(DOMAIN, 'FastLaps', 'Full bundle path failed, trying relative path');
      try {
        router.push({
          url: 'pages/RacesScreen'
        });
        hilog.info(DOMAIN, 'FastLaps', 'Navigating to RacesScreen using relative path');
      } catch (error2) {
        hilog.error(DOMAIN, 'FastLaps', 'Both navigation methods failed: %{public}s', JSON.stringify(error2));
      }
    }
  }

  onDriversButtonClicked(): void {
    // TODO: Implement drivers navigation
  }

  onTeamsButtonClicked(): void {
    // TODO: Implement teams navigation
  }

  onNewsButtonClicked(): void {
    // TODO: Implement news navigation
  }

  onAboutButtonClicked(): void {
    // TODO: Implement about navigation
  }
}
export class App {
  configureRouter(config, router) {
    config.title = 'Aurelia';
    config.map([
      { route: ['', 'timer'], name: 'timer',      moduleId: 'timer',      nav: true, title: 'Timer' }
    ]);

    this.router = router;
  }
}

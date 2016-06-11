import {Router, RouterConfiguration} from 'aurelia-router'

export class App {
  router: Router;
  
  configureRouter(config: RouterConfiguration, router: Router) {
    config.title = 'Time tracker';
    config.map([
      { route: ['', 'timer'], name: 'timer',      moduleId: 'timer',      nav: true, title: 'Timer' },
    ]);

    this.router = router;
  }
}

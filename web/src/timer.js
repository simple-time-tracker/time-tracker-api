import {inject} from 'aurelia-framework';
import {computedFrom} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
import {AureliaConfiguration} from 'aurelia-configuration';
import 'fetch';

@inject(HttpClient, AureliaConfiguration)
export class TimeTracker {
    descriptionPlaceholder = 'What are you working on?';
    self = this;
    currentProject = null;
    currentDescription = '';
    currentlyTrackingEntry = null;
    projects = [];
    timeEntries = [];

    constructor(http:HttpClient, config:AureliaConfiguration) {
        this.config = config;
        http.configure(config => {
            config
                .useStandardConfiguration()
                .withBaseUrl(this.config.get('api.endpoint'));
        });
        this.http = http;
    }

    activate() {
        return Promise.all([
            this.loadProjects(),
            this.loadCurrentlyTrackingEntry(),
            this.loadTimeEntries()
        ]);
    }

    loadCurrentlyTrackingEntry() {
        this.http.fetch("entries/current")
            .then(response => {
                if (response.status == 200) {
                    return response.json()
                }
                return null;
            }).then(current => {
            this.currentlyTrackingEntry = current;
            if (current) {
                this.currentDescription = current.description;
            }
        })
    }

    loadTimeEntries() {
        this.http.fetch('entries')
            .then(response => response.json())
            .then(entries =>this.timeEntries = entries.reverse())
    }

    loadProjects() {
        this.http.fetch('projects/active')
            .then(response => response.json())
            .then(projects => {
                if (projects.length > 0) {
                    this.projects = projects;
                }
                else this.projects = [{"id": null, "name": "Select project"}]
            })
    }

    @computedFrom('currentlyTrackingEntry')
    get isCurrentlyTracking() {
        return this.currentlyTrackingEntry != null
    }

    @computedFrom('projects')
    get isProjectSelected() {
        if (this.projects.length > 1) {
            return true;
        }
        else {
            if (this.projects.length == 1) {
                return this.projects[0].id != null;
            }
            return false;
        }
    }

    @computedFrom('currentlyTrackingEntry')
    get currentlyTrackingEntryMessage() {
        if (this.currentlyTrackingEntry != null) {
            return `Currently working on ${this.currentlyTrackingEntry.project.name} project. 
                    Current task: ${this.currentlyTrackingEntry.description}.
                    Start date  ${(new Date(this.currentlyTrackingEntry.startDate).toLocaleString())}`
        }
        else return '';
    }

    @computedFrom('isCurrentlyTracking')
    get currentToggleLabel() {
        if (this.isCurrentlyTracking) {
            return "Stop tracking"
        }
        else return "Start tracking"
    }

    getStartUrl() {
        return 'entries/start/' + this.currentProject + "?description=" + this.currentDescription
    }

    toggleTracking() {
        if (this.isCurrentlyTracking)
            this.stop();
        else this.start();
    }

    start() {
        this.http.fetch(this.getStartUrl(), {
            method: 'post'
        }).then(response => {
            if (response.status == 201) {
                this.loadCurrentlyTrackingEntry();
                this.loadTimeEntries();
            }
        })
    }

    stop() {
        this.http.fetch('entries/stop', {
            method: 'post'
        }).then(response => {
            if (response.status == 200) {
                this.currentDescription = '';
                this.loadCurrentlyTrackingEntry();
                this.loadTimeEntries();
            }
        })
    }

}

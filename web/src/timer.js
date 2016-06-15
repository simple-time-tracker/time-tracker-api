import {inject} from 'aurelia-framework';
import {computedFrom} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
//import {Configure} from "aurelia-configuration";
import moment from 'moment';
import 'fetch';

@inject(HttpClient)
export class TimeTracker {
    descriptionPlaceholder = 'What are you working on?';

    currentProject = null;
    currentDescription = '';
    currentlyTrackingEntry = null;
    projects = [];
    timeEntries = [];

    constructor(http:HttpClient, moment:moment) {
        http.configure(config => {
            config
                .useStandardConfiguration()
                .withBaseUrl('http://localhost:8080/api/');
        });
        this.http = http;
    }

    activate() {
        return Promise.all([
            this.loadCurrentlyTrackingEntry(),
            this.loadProjects(),
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
            }).then(current => this.currentlyTrackingEntry = current)
    }

    loadTimeEntries() {
        this.http.fetch('entries')
            .then(response => response.json())
            .then(entries =>this.timeEntries = entries.reverse())
    }

    loadProjects() {
        this.http.fetch('projects')
            .then(response => response.json())
            .then(projects => {
                if (projects.length > 0) {
                    this.projects = projects
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

    getStartUrl() {
        return 'entries/start/' + this.currentProject.id + "?description=" + this.currentDescription
    }

    start() {
        this.http.fetch(this.getStartUrl(), {
            method: 'post',
        }).then(response => {
            if (response.status == 201) {
                this.currentDescription = '';
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
                this.loadCurrentlyTrackingEntry();
                this.loadTimeEntries();
            }
        })
    }

}

import {autoinject} from 'aurelia-framework';
import {computedFrom} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
import {Configure} from "aurelia-configuration";
import 'fetch';

@autoinject(HttpClient, Configure)
export class TimeTracker {

    currentProject = null;
    currentDescription = null;
    currentlyTrackingEntry = null;
    projects = [];
    timeEntries = [];

    constructor(private http:HttpClient, private config: Configure) {
        console.log(config.get('api.endpoint'))
        http.configure(config => {
            config
                .useStandardConfiguration()
                .withBaseUrl(this.config.get('api.endpoint'));
        });
    }

    activate() {
        return Promise.all([
            this.loadCurrenlyTrackingEntry()
            ,
            this.http.fetch('projects')
                .then(response => response.json())
                .then(projects => this.projects = projects),
            this.loadTimeEntries()
        ]);
    }

    loadCurrenlyTrackingEntry() {
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

    @computedFrom('currentlyTrackingEntry')
    get isCurrentlyTracking() {
        return this.currentlyTrackingEntry != null
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
                this.loadCurrenlyTrackingEntry();
                this.loadTimeEntries();
            }
        })
    }

    stop() {
        this.http.fetch('entries/stop', {
            method: 'post'
        }).then(response => {
            if (response.status == 200) {
                this.loadCurrenlyTrackingEntry();
                this.loadTimeEntries();
            }
        })
    }

}

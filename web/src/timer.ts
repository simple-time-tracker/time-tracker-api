import {autoinject} from 'aurelia-framework';
import {computedFrom} from 'aurelia-framework';
import {HttpClient} from 'aurelia-fetch-client';
import 'fetch';

@autoinject(HttpClient)
export class TimeTracker {

    currentProject = null;
    currentDescription = null;
    currentlyTrackingEntry = null;
    projects = [];

    constructor(private http:HttpClient) {
        http.configure(config => {
            config
                .useStandardConfiguration()
                .withBaseUrl('http://localhost:8080/api/');
        });
    }

    activate() {
        return Promise.all([
            this.getCurrentlyTrackingEntry()
            ,
            this.http.fetch('projects')
                .then(response => response.json())
                .then(projects => this.projects = projects),
        ]);
    }

    getCurrentlyTrackingEntry() {
        this.http.fetch("entries/current")
            .then(response => {
                if (response.status == 200) {
                    return response.json()
                }
                return null;
            }).then(current => this.currentlyTrackingEntry = current)
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
                this.getCurrentlyTrackingEntry()
            }
        })
    }

    stop() {
        this.http.fetch('entries/stop', {
            method: 'post'
        }).then(response => {
            if (response.status == 200) {
                this.getCurrentlyTrackingEntry()
            }
        })
    }

}

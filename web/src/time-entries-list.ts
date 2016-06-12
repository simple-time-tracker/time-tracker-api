import {bindable} from 'aurelia-framework';

export class TimeEntriesList {
    @bindable entries = [];

    getDifference(entry) {
        if (entry.endDate != null) {
            var diff = new Date(entry.endDate).getTime() - new Date(entry.startDate).getTime();
            return this.secondsToTime(diff / 1000)
        }
        else return '';
    }


    secondsToTime(secs) {
        secs = parseInt(secs);
        var hours = Math.floor(secs / (60 * 60));

        var divisor_for_minutes = secs % (60 * 60);
        var minutes = Math.floor(divisor_for_minutes / 60);

        var divisor_for_seconds = divisor_for_minutes % 60;
        var seconds = Math.ceil(divisor_for_seconds);

        return `${hours} hours, ${minutes} minutes, ${seconds} seconds`;

    }
}


import {bindable} from 'aurelia-framework';
import moment from 'moment';

export class TimeEntriesList {
    @bindable entries = [];

    constructor(moment: moment) {
        this.moment = moment;
    }

    getDifference(entry) {
        if (entry.endDate != null) {
            var diff = new Date(entry.endDate).getTime() - new Date(entry.startDate).getTime();
            return this.getDuration(diff);
        }

        else return '';
    }

    shortDateTime(date) {
        return date ? moment(date).format('MM/DD HH:mm'): '';
    }


    getDuration(date) {
        var duration = moment.duration(date);
        var hours = duration.days() > 0 ? Math.floor(duration.asHours()) : duration.hours();
        var minutes = duration.minutes();
        var seconds = duration.seconds();

        return `${this.convertTimeUnitToString(hours)}:${this.convertTimeUnitToString(minutes)}:${this.convertTimeUnitToString(seconds)}`;
    }

    convertTimeUnitToString(unit) {
        if (('' + unit).length == 1)
            return '0' + unit;
        else return unit == '0' ? '00': unit
    }
}


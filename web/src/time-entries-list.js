import {bindable} from 'aurelia-framework';
import {inject} from 'aurelia-framework';
import {HttpClient} from 'aurelia-http-client';
import {AureliaConfiguration} from 'aurelia-configuration';
import moment from 'moment/moment-timezone';

@inject(AureliaConfiguration, moment)
export class TimeEntriesList {
    @bindable entries = [];

    constructor(config:AureliaConfiguration, moment: moment) {
        this.config = config;
        this.moment = moment;

        this.http = new HttpClient();
        this.http.configure(cfg => {
            cfg.withBaseUrl(this.config.get('api.endpoint'));
        });
    }

    getDifference(entry) {
        if (entry.endDate != null) {
            var diff = new Date(entry.endDate).getTime() - new Date(entry.startDate).getTime();
            return this.getDuration(diff);
        }

        else return '';
    }

    shortDateTime(date) {
        return date ? moment.tz(moment.utc(date), moment.tz.guess()).format('MM/DD HH:mm'): '';
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

    deleteEntry(time_entry_id) {
      var self = this;

      this.http.delete('entries/' + time_entry_id)
          .then(response => {
            self.entries  = self.entries.filter(function(element) {
                return element.id !== time_entry_id
            })

          })
    }
}

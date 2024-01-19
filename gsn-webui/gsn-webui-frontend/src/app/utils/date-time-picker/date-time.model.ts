import { NgbTimeStruct, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';

export interface NgbDateTimeStruct extends NgbDateStruct, NgbTimeStruct { }

export class DateTimeModel implements NgbDateTimeStruct {
  year: number = 2023;
  month: number = 1;
  day: number = 1;
  hour: number = 0;
  minute: number = 0;
  second: number = 0;

  timeZoneOffset: number = 0;

  public constructor(init?: Partial<DateTimeModel>) {
    Object.assign(this, init);
  }

  public static fromLocalString(dateString: string): DateTimeModel {
    const date = new Date(dateString);

    const isValidDate = !isNaN(date.valueOf());

    if (!dateString || !isValidDate) {
      return new DateTimeModel({
        year: 0,
        month: 0,
        day: 0,
        hour: 0,
        minute: 0,
        second: 0,
        timeZoneOffset: 0
      });
    }

    return new DateTimeModel({
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      day: date.getDate(),
      hour: date.getHours(),
      minute: date.getMinutes(),
      second: date.getSeconds(),
      timeZoneOffset: date.getTimezoneOffset()
    });
  }

  private isInteger(value: any): value is number {
    return typeof value === 'number' && isFinite(value) && Math.floor(value) === value;
  }

  public toString(): string {
    if (this.isInteger(this.year) && this.isInteger(this.month) && this.isInteger(this.day)) {
      const year = this.year.toString().padStart(2, '0');
      const month = this.month.toString().padStart(2, '0');
      const day = this.day.toString().padStart(2, '0');

      if (!this.hour) {
        this.hour = 0;
      }
      if (!this.minute) {
        this.minute = 0;
      }
      if (!this.second) {
        this.second = 0;
      }
      if (!this.timeZoneOffset) {
        this.timeZoneOffset = new Date().getTimezoneOffset();
      }

      const hour = this.hour.toString().padStart(2, '0');
      const minute = this.minute.toString().padStart(2, '0');
      const second = this.second.toString().padStart(2, '0');

      const tzo = -this.timeZoneOffset;
      const dif = tzo >= 0 ? '+' : '-',
        pad = function (num: string | number) {
          const norm = Math.floor(Math.abs(Number(num)));
          return (norm < 10 ? '0' : '') + norm;
        };

      const isoString = `${pad(year)}-${pad(month)}-${pad(day)}T${pad(hour)}:${pad(minute)}:${pad(second)}${dif}${pad(tzo / 60)}:${pad(tzo % 60)}`;
      return isoString;
    }

    return '';
  }
}
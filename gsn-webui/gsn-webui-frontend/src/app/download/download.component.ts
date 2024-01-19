import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { DownloadService } from '../services/download.service';
import { SensorService } from '../services/sensor.service';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-download',
  templateUrl: './download.component.html',
  styleUrls: ['./download.component.scss'],
})
export class DownloadComponent implements OnInit {

  formGroup = this.formBuilder.group({
    selectedSensors: [[], Validators.required],
    startDate: [new Date(new Date().getTime() - 1000 * 60 * 60), Validators.required],
    endDate: [new Date(), Validators.required],
  });

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private downloadService: DownloadService,
    private sensorService: SensorService,
    private formBuilder: FormBuilder
  ) { }

  sensors: string[] = [];

  ngOnInit(): void {
    this.getSensors();
  }

  getSensors(): void {
    this.sensorService.getSensors().subscribe(
      (data: any) => {
        var features = data.features;
        features.forEach((feature: any) => {
          this.sensors.push(feature.properties.vs_name);
        });
      },
      (error: any) => {
        console.error(error);
      }
    );
  }

  downloadData(): void {

    const selectedSensors: string[] = this.formGroup.get('selectedSensors')?.value || [];
    const startDate: string = this.formatDate(this.formGroup.get('startDate')?.value)
    const endDate: string = this.formatDate(this.formGroup.get('endDate')?.value)

    if (this.formGroup.valid) {
      this.downloadService.downloadMultiple(selectedSensors, startDate, endDate);
    }
  }

  formatDate(date: any): string {
    const new_date = new Date(date).toJSON();
    return new_date.slice(0, 19);
  }

}

import { Component, OnInit } from '@angular/core';
import { ChartOptions, ChartType, ChartDataset } from 'chart.js';
import { SensorService } from '../services/sensor.service';
import { FormBuilder, Validators } from '@angular/forms';

interface SensorData {
  vs_name: string;
  geographical: string;
  latitude: string;
  longitude: string;
  description: string;
  fields: Field[];
  stats: Stats;
  values: number[][];
}

interface Field {
  name: string;
  type: string;
  unit: string | null;
}

interface Stats {
  "start-datetime": number;
  "end-datetime": number;
}

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.scss']
})
export class CompareComponent implements OnInit {

  constructor(
    private sensorService: SensorService,
    private formBuilder: FormBuilder
  ) { }
  
  ngOnInit(): void {
    this.getSensors();
  }

  formGroup = this.formBuilder.group({
    startDate: [new Date(new Date().getTime() - 1000 * 60 * 60), Validators.required],
    endDate: [new Date(), Validators.required],
  });

  sensors: string[] = [];
  sensorData: { [key: string]: any } = {};

  showFilterOut : boolean = false;
  filterTermsOut : any;
  filterTermsIn : any;

  lineChartData: any[] = []
  lineChartLabels: string[] = [];
  lineChartOptions: any = {
    responsive: true
  };
  lineChartLegend = true;

  getSensors(): void {
    this.sensorService.getSensors().subscribe(
      (data: any) => {
        var features = data.features;
        features.forEach((feature: any) => {
          this.sensors.push(feature.properties.vs_name);
        });
        this.getSensorData();
      },
      (error: any) => {
        console.error(error);
      }
    );
  }

  getSensorData(): void {

    const from: string = this.formatDate(this.formGroup.get('startDate')?.value)
    const to: string = this.formatDate(this.formGroup.get('endDate')?.value)

    console.log("FROM", from)
    console.log("TO", to)

    for (var i = 0; i < this.sensors.length; i++) {
    
      var sensorName = this.sensors[i];

      this.sensorService.getSensorData(sensorName, from, to).subscribe(
        (data: any) => {
          
          var sensorName = data.properties.vs_name;
          this.sensorData[sensorName] = {};

          var value_dict = this.parseData(data)
          this.sensorData[sensorName] = value_dict;
          this.initChart(sensorName, value_dict);

        },
        (error: any) => {
          console.error(error);
        }
      );
    }
  }

  initChart(sensor_name : string, value_dict : any): void{

    Object.entries(value_dict).forEach(([key, value]) => {
      if(key !== 'timestamp' && key !== 'time'){
        var random_color = this.getRandomColor();
        this.lineChartData.push({
          data: value,
          label: sensor_name + key,
          backgroundColor: random_color,
          borderColor: random_color
        },)
      }

      if(key == 'time'){
        this.lineChartLabels = value as string[];
      }

    });
  }

  getRandomColor() : string {
    const red = Math.floor(Math.random() * 256);   // Random value between 0 and 255
    const green = Math.floor(Math.random() * 256); // Random value between 0 and 255
    const blue = Math.floor(Math.random() * 256);  // Random value between 0 and 255
  
    return `rgba(${red}, ${green}, ${blue}, 0.5)`;  // Construct the RGBA color string
  }

  parseData(data:any): any {
    var sensorName = data.properties.vs_name;
    var value_dict: { [key: string]: any } = {};
   
    // Loop over field names
    for (var i = 0; i < data.properties.fields.length; i++){

      var field_name = data.properties.fields[i].name;
      var value_list: any[] = [];

      // Loop over all values of sensor, index of field name = index of value
      data.properties.values.forEach((value:any) => {
        value_list.push(value[i])
      });

      value_dict[field_name] = value_list;
    }
    return value_dict;
  }


  submit():void {
    this.sensorData = {};
    this.lineChartData = [];
    this.getSensorData();
  }

  update(): void {
    console.log("UPDATE");
  }

  remove(key : any): void {
    console.log("REMOVE");
  }

  formatDate(date: any): string {
    const new_date = new Date(date).toJSON();
    return new_date.slice(0, 19);
  }

}

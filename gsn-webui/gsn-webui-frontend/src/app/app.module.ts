import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SensorListComponent } from './sensor-list/sensor-list.component';
import { HttpClientModule } from '@angular/common/http';
import { SensorDetailComponent } from './sensor-detail/sensor-detail.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MapComponent } from './map/map.component';
import { FormsModule } from '@angular/forms';
import { NgbDatepickerModule, NgbTimepickerModule , NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';
import { DateTimePickerComponent } from './utils/date-time-picker/date-time-picker.component';
import { DownloadComponent } from './download/download.component';
import { NgChartsModule } from 'ng2-charts';
import { CompareComponent } from './compare/compare.component';

@NgModule({
  declarations: [
    AppComponent,
    SensorListComponent,
    SensorDetailComponent,
    DashboardComponent,
    MapComponent,
    DateTimePickerComponent,
    DownloadComponent,
    CompareComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    NgChartsModule,
    NgbDatepickerModule,
    NgbTimepickerModule,
    NgbPopoverModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

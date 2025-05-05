import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Chart, ChartData, registerables } from 'chart.js';
import { AuthService } from '../../../services/auth.service';
import * as FileSaver from 'file-saver';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../environments/environment';


Chart.register(...registerables);

interface GraphReport {
  month: string;
  date?: string;
  fuelingExpense: number;
  maintenanceExpense: number;
  milesDriven: number;
}

interface VehicleMileage {
  vehicleNumber: string;
  modelYear: number;
  currentMileage: number;
}

interface MaintenanceEvent {
  vehicleNumber: string;
  date: string;
  maintenanceCost: number;
  maintenanceDescription: string;
}

interface GovernmentReport {
  vehicleType: string;
  vehicleDescription: string;
  lessThan8500: number;
  greaterThan8500: number;
  milesTravelled: number;
  gasOrDieselGallons: number;
  altFuelGallons: number;
  gasOrDieselCost: number;
  altFuelCost: number;
  maintenanceCost: number;
}

interface GroupedGovReport {
  vehicleType: string;
  values: GovernmentReport[];
}


@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css'],
  standalone: false
})
export class ReportsComponent implements OnInit {
  showReportsDropdown: boolean = false;
  selectedSection: 'graphs' | 'reports' | 'government' |null = null;
  selectedGraph: string = '';  
  selectedReport: "mileage" | "maintenance" | 'fuel-efficiency' | null = null;
  selectedYear: number = new Date().getFullYear();
  selectedMonth: string = 'All'; 
  errorMessage: string = '';


  // Graph Data
  graphReports: GraphReport[] = [];
  graphChartData: ChartData<'line'> = { labels: [], datasets: [] };

  // Reports Data
  vehicleMileageData: VehicleMileage[] = [];
  maintenanceEventsData: MaintenanceEvent[] = [];
  fuelEfficiencyData: { vehicleNumber: string; fuelEfficiency: number }[] = [];

  fuelEfficiencyFilter = {
    vehicleNumber: '',
    minMpg: null as number | null,
    maxMpg: null as number | null,
  };

  maintenanceFilter = {
    vehicleNumber: '',
    startDate: '',
    endDate: '',
    minCost: null,
    maxCost: null,
  };  

  maintenanceFilterDate = {
    startDate: null,
    endDate: null
  };

  mileageFilter = {
    vehicleNumber: '',
    minMileage: null,
    maxMileage: null
  };

  govFilter = {
    fiscalYear: new Date().getFullYear(),
    month: ''
  };

  months = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'];

  governmentData: GovernmentReport[] = [];
  groupedGovernmentData: { vehicleType: string, values: GovernmentReport[] }[] = [];
  //govFilter = { fiscalYear: null, month: '' };

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
  }

  toggleReportsDropdown() {
    this.showReportsDropdown = !this.showReportsDropdown;
  }

  selectReportType(type: 'graphs' | 'reports' | 'government') {
    this.selectedSection = type;
    this.showReportsDropdown = false; // Hide dropdown once selected
    if (type === 'graphs') {
      this.selectedYear = 2025;
      this.selectedMonth = 'All';
      this.selectedGraph = 'maintenanceExpense';
      this.fetchGraphReports();
    } else if(type === 'reports') {
      this.selectedReport = 'maintenance';
      //this.fetchVehicleMileageReport();
      this.fetchMaintenanceEventsReport();
    } else if(type === 'government') {
      this.fetchGovernmentReport();
    }
  }

  selectReport(report: 'mileage' | 'maintenance' | 'fuel-efficiency') {
    this.selectedReport = report;

    if (report === 'mileage') {
      this.fetchVehicleMileageReport();
    } else if (report === 'maintenance') {
      this.fetchMaintenanceEventsReport();
    } else if(report === 'fuel-efficiency') {
      this.fetchFuelEfficiency(); // ✅ Auto-fetch on button click
    }
  }

  getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  fetchGraphReports() {
    this.http.get<GraphReport[]>(`${environment.apiBaseUrl}/api/admin/graph/report/${this.selectedYear}`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => {
          this.graphReports = data;
          this.updateGraphData();
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to load graph reports.';
        }
      });
  }

  updateGraphData() {
    if (this.selectedMonth !== 'All') {
      const monthMap: { [key: string]: number } = {
        'January': 1, 'February': 2, 'March': 3, 'April': 4, 'May': 5, 'June': 6,
        'July': 7, 'August': 8, 'September': 9, 'October': 10, 'November': 11, 'December': 12
      };

      const selectedMonthNumber = monthMap[this.selectedMonth];

      this.http.get<GraphReport[]>(`${environment.apiBaseUrl}/api/admin/graph/daily-report/${this.selectedYear}/${selectedMonthNumber}`, 
        { headers: this.getHeaders() }
      ).subscribe({
        next: (data) => {
          this.graphReports = data;
          this.processGraphData();
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to load daily graph reports.';
        }
      });
    } else {
      this.processGraphData();
    }
  }

  processGraphData() {
    let labels: string[] = [];
    let datasetLabel = '';
    let dataValues: number[] = [];

    if (this.selectedMonth === 'All') {
      labels = this.graphReports.map(report => report.month);
    } else {
      labels = this.graphReports.map(report => report.date)
        .filter((date): date is string => date !== undefined)
        .filter((_, index) => index % 5 === 0);
    }

    switch (this.selectedGraph) {
      case 'fuelExpense':
        datasetLabel = 'Fuel Expense ($)';
        dataValues = this.graphReports.map(report => report.fuelingExpense);
        break;
      case 'milesDriven':
        datasetLabel = 'Miles Driven';
        dataValues = this.graphReports.map(report => report.milesDriven);
        break;
      case 'maintenanceExpense':
        datasetLabel = 'Maintenance Expense ($)';
        dataValues = this.graphReports.map(report => report.maintenanceExpense);
        break;
      default:
        datasetLabel = '';
        dataValues = [];
    }

    this.graphChartData = {
      labels: labels,
      datasets: [
        {
          label: datasetLabel,
          data: dataValues,
          borderColor: 'blue',
          backgroundColor: 'rgba(0, 0, 255, 0.2)',
          fill: true,
          tension: 0.3
        }
      ]
    };
  }

  selectGraph(graphType: string) {
    this.selectedGraph = graphType;
    this.updateGraphData();
  }

  viewGraphs() {
    this.selectedSection = 'graphs';
    this.fetchGraphReports(); // Ensure graph data is loaded when selected
  }

  closeGraph() {
    this.selectedGraph = '';
  }

  /* fetchVehicleMileageReport() {
    this.http.get<VehicleMileage[]>(`${environment.apiBaseUrl}/api/admin/reports/vehicles-mileage`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => {
          this.vehicleMileageData = data;
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to load vehicle mileage report.';
        }
      });
  } */

  fetchVehicleMileageReport() {
    const params: any = {};
    if (this.mileageFilter.vehicleNumber?.trim()) {
      params.vehicleNumber = this.mileageFilter.vehicleNumber.trim();
    }
    if (this.mileageFilter.minMileage != null) {
      params.minMileage = this.mileageFilter.minMileage;
    }
    if (this.mileageFilter.maxMileage != null) {
      params.maxMileage = this.mileageFilter.maxMileage;
    }
  
    this.http.get<VehicleMileage[]>(`${environment.apiBaseUrl}/api/admin/reports/vehicles-mileage/filter`, {
      params,
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => {
        this.vehicleMileageData = data;
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to filter vehicle mileage report.';
      }
    });
  }
  
  downloadFilteredVehicleMileageReport() {
    const params: any = {};
  
    if (this.mileageFilter.vehicleNumber?.trim()) {
      params.vehicleNumber = this.mileageFilter.vehicleNumber.trim();
    }
  
    if (this.mileageFilter.minMileage != null) {
      params.minMileage = this.mileageFilter.minMileage;
    }
  
    if (this.mileageFilter.maxMileage != null) {
      params.maxMileage = this.mileageFilter.maxMileage;
    }
  
    this.http.get(`${environment.apiBaseUrl}/api/admin/export/vehicles-mileage/download`, {
      params,
      headers: this.getHeaders(),
      responseType: 'blob'
    }).subscribe(blob => {
      FileSaver.saveAs(blob, 'NWMSU_Vehicle_Mileage_Report.xlsx');
    });
  }
  

  /*
  fetchMaintenanceEventsReport() {
    this.http.get<MaintenanceEvent[]>(`${environment.apiBaseUrl}/api/admin/reports/maintenance-events`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => {
          this.maintenanceEventsData = data;
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Failed to load maintenance events report.';
        }
      });
  } */

  fetchMaintenanceEventsReport() {
    const params: any = {};
  
    if (this.maintenanceFilter.vehicleNumber.trim()) {
      params.vehicleNumber = this.maintenanceFilter.vehicleNumber.trim();
    }
    if (this.maintenanceFilter.startDate) {
      params.startDate = this.maintenanceFilter.startDate;
    }
    if (this.maintenanceFilter.endDate) {
      params.endDate = this.maintenanceFilter.endDate;
    }
    if (this.maintenanceFilter.minCost != null) {
      params.minCost = this.maintenanceFilter.minCost;
    }
    if (this.maintenanceFilter.maxCost != null) {
      params.maxCost = this.maintenanceFilter.maxCost;
    }
  
    const hasFilter = Object.keys(params).length > 0;
    const url = hasFilter
      ? `${environment.apiBaseUrl}/api/admin/reports/maintenance-events/filter`
      : `${environment.apiBaseUrl}/api/admin/reports/maintenance-events`;
  
    this.http.get<MaintenanceEvent[]>(url, {
      params,
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => {
        this.maintenanceEventsData = data;
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to load maintenance events report.';
      }
    });
  }
      
  
  resetMaintenanceFilters() {
    this.maintenanceFilter = {
      vehicleNumber: '',
      startDate: '',
      endDate: '',
      minCost: null,
      maxCost: null
    };
    this.fetchMaintenanceEventsReport();
  }      

  downloadReportDuplicate(type: "vehicles" | "maintenance") {
    const url = type === 'vehicles' ? 
      `${environment.apiBaseUrl}/api/admin/export/vehicles/excel` : 
      `${environment.apiBaseUrl}/api/admin/export/maintenance/pdf`;

    window.open(url, '_blank');
  }

  downloadReport(reportType: 'vehicles' | 'maintenance') {
    let apiUrl = reportType === 'vehicles'
      ? `${environment.apiBaseUrl}/api/admin/export/vehicles/excel`
      : `${environment.apiBaseUrl}/api/admin/export/maintenance/pdf`;

    this.http.get(apiUrl, { headers: this.getHeaders(), responseType: 'blob' }).subscribe({
      next: (response) => {
        const fileName = reportType === 'vehicles' ? 'Vehicle_Mileage_Report.xlsx' : 'Maintenance_Events_Report.pdf';
        FileSaver.saveAs(response, fileName);
      },
      error: () => {
        this.errorMessage = 'Failed to download the report.';
      }
    });
  }

  fetchFuelEfficiency() {
    const params: any = {};
  
    if (this.fuelEfficiencyFilter.vehicleNumber?.trim()) {
      params.vehicleNumber = this.fuelEfficiencyFilter.vehicleNumber.trim();
    }
  
    if (this.fuelEfficiencyFilter.minMpg != null) {
      params.minMpg = this.fuelEfficiencyFilter.minMpg;
    }
  
    if (this.fuelEfficiencyFilter.maxMpg != null) {
      params.maxMpg = this.fuelEfficiencyFilter.maxMpg;
    }
  
    this.http.get<any[]>(`${environment.apiBaseUrl}/api/reports/fuel-efficiency`, {
      params,
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => this.fuelEfficiencyData = data,
      error: (error) => console.error('Fuel Efficiency Error:', error)
    });
  }
  
  
  downloadFuelEfficiencyReport() {
    const params: any = {};

    if (this.fuelEfficiencyFilter.vehicleNumber?.trim()) {
      params.vehicleNumber = this.fuelEfficiencyFilter.vehicleNumber.trim();
    }

    if (this.fuelEfficiencyFilter.minMpg != null) {
      params.minMpg = this.fuelEfficiencyFilter.minMpg;
    }

    if (this.fuelEfficiencyFilter.maxMpg != null) {
      params.maxMpg = this.fuelEfficiencyFilter.maxMpg;
    }

    this.http.get(`${environment.apiBaseUrl}/api/reports/fuel-efficiency/download`, {
      params,
      headers: this.getHeaders(),
      responseType: 'blob'
    }).subscribe(blob => {
      const fileName = 'NWMSU_Fuel_Efficiency_Report.xlsx';
      FileSaver.saveAs(blob, fileName);
    });
  }

  clearFuelEfficiencyFilters() {
    this.fuelEfficiencyFilter.vehicleNumber = '';
    this.fuelEfficiencyFilter.minMpg = null;
    this.fuelEfficiencyFilter.maxMpg = null;
    this.fetchFuelEfficiency(); // re-fetch full data
  }

  isFuelEfficiencyFilterActive(): boolean {
    return (
      !!this.fuelEfficiencyFilter.vehicleNumber?.trim() ||
      this.fuelEfficiencyFilter.minMpg != null ||
      this.fuelEfficiencyFilter.maxMpg != null
    );
  }

  isVehicleMileageFilterActive(): boolean {
    return (
      !!this.mileageFilter.vehicleNumber?.trim() ||
      this.mileageFilter.minMileage != null ||
      this.mileageFilter.maxMileage != null
    );
  }

  isMaintenanceFilterActive(): boolean {
    return (
      !!this.maintenanceFilter.vehicleNumber?.trim() ||
      !!this.maintenanceFilter.startDate ||
      !!this.maintenanceFilter.endDate ||
      this.maintenanceFilter.minCost != null ||
      this.maintenanceFilter.maxCost != null
    );
  }

  formatDate(date: NgbDateStruct): string {
    return `${date.year}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}`;
  }
  
  downloadFilteredMaintenanceReport() {
    const params: any = {};
  
    if (this.maintenanceFilter.vehicleNumber?.trim()) {
      params.vehicleNumber = this.maintenanceFilter.vehicleNumber.trim();
    }
    if (this.maintenanceFilter.startDate) {
      params.startDate = this.maintenanceFilter.startDate;
    }
    if (this.maintenanceFilter.endDate) {
      params.endDate = this.maintenanceFilter.endDate;
    }
    if (this.maintenanceFilter.minCost != null) {
      params.minCost = this.maintenanceFilter.minCost;
    }
    if (this.maintenanceFilter.maxCost != null) {
      params.maxCost = this.maintenanceFilter.maxCost;
    }
  
    this.http.get(`${environment.apiBaseUrl}/api/admin/export/maintenance/pdf`, {
      params,
      headers: this.getHeaders(),
      responseType: 'blob'
    }).subscribe(blob => {
      const fileName = 'NWMSU Vehicle Maintenance_Report.pdf';
      FileSaver.saveAs(blob, fileName);
    });
  }
  
  fetchGovernmentReport() {
    const params: any = {};
    if (this.govFilter.fiscalYear) {
      params.fiscalYear = this.govFilter.fiscalYear;
    }
    if (this.govFilter.month) {
      params.month = this.govFilter.month;
    }
  
    this.http.get<GovernmentReport[]>(`${environment.apiBaseUrl}/api/reports/government`, {
      params,
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => {
        this.governmentData = data;
        this.groupGovernmentData();
      },
      error: (error) => console.error('Government Report Error:', error)
    });
  }
  
  groupGovernmentData() {
    const grouped: { [key: string]: GovernmentReport[] } = {};
  
    for (const row of this.governmentData) {
      if (!grouped[row.vehicleType]) {
        grouped[row.vehicleType] = [];
      }
      grouped[row.vehicleType].push(row);
    }
  
    this.groupedGovernmentData = Object.entries(grouped).map(([vehicleType, values]) => ({
      vehicleType,
      values
    }));

    /*this.groupedGovernmentData = Object.entries(grouped).map(([type, values]) => {
      const filteredValues = type === 'Electric'
        ? values.filter(v => v.vehicleDescription === 'LDTs, Vans, SUVs')
        : values;
    
      return {
        vehicleType: type,
        values: filteredValues
      };
    });*/
    
  }
  
  downloadGovernmentReport() {
    const params: any = {};
    if (this.govFilter.fiscalYear) {
      params.fiscalYear = this.govFilter.fiscalYear;
    }
    if (this.govFilter.month) {
      params.month = this.govFilter.month;
    }
  
    this.http.get(`${environment.apiBaseUrl}/api/reports/government/download`, {
      params,
      headers: this.getHeaders(),
      responseType: 'blob'
    }).subscribe(blob => {
      FileSaver.saveAs(blob, 'Government_Report.xlsx');
    });
  }
}

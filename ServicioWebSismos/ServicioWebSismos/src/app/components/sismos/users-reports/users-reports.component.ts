import { Component, OnInit } from '@angular/core';

//service 
import { SismoService} from '../../../services/sismo.service';

//Class sismo
import {Sismo} from '../../../models/sismo';
import { element } from 'protractor';
import { Reporte } from 'src/app/models/reporte';
import { report } from 'process';

@Component({
  selector: 'app-users-reports',
  templateUrl: './users-reports.component.html',
  styleUrls: ['./users-reports.component.scss']
})
export class UsersReportsComponent implements OnInit {

  p1: number = 1;
  sismoList!: Sismo[];
  constructor(private sismoService: SismoService) { }

  ngOnInit(): void {
    this.sismoService.getSismos().snapshotChanges().subscribe(item=>{
      this.sismoList = [];
      item.forEach(element =>{
        let x:any = element.payload.toJSON();
        var reporteros: Array<Reporte> = []
        
        Object.keys(x.reporteUsuario).map(key => {
          var y = x.reporteUsuario[key] as Reporte
          reporteros.push(y)
          console.log(y)
        })

        x["$key"] = element.key;
        // console.log(x)
        let sismo :Sismo = x as Sismo
        sismo.reporteUsuario = reporteros
        console.log(sismo)
        this.sismoList.push(sismo);
      })
    });
  }

}

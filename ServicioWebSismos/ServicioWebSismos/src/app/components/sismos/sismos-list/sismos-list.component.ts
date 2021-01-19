import { Component, OnInit } from '@angular/core';

//service 
import { SismoService} from '../../../services/sismo.service';
import {ToastrService} from 'ngx-toastr';

//Class sismo
import {Sismo} from '../../../models/sismo';

@Component({
  selector: 'app-sismos-list',
  templateUrl: './sismos-list.component.html',
  styleUrls: ['./sismos-list.component.scss']
})
export class SismosListComponent implements OnInit {

  p: number = 1;
  sismoList!: Sismo[];
  
  constructor( private sismoService: SismoService, private toast:ToastrService) { }

  ngOnInit(): void {
    this.sismoService.getSismos().snapshotChanges().subscribe(item=>{
      this.sismoList = [];
      item.forEach(element =>{
        let x:any = element.payload.toJSON();
        x["$key"] = element.key;
        this.sismoList.push(x as Sismo)
      })
    })
  }

  //Editar los datos de sismo
  onEdit(sismo: Sismo){
    this.sismoService.selectedSismo = Object.assign({}, sismo);
  }
  //Borrar un sismo
  onDelete($key:string){
    if(confirm('Seguro deseas eliminar el sismo')){
      this.sismoService.deleteSismo($key); 
      this.toast.success('Successful Operation', 'Sismo Eliminado');
    }

  }

}

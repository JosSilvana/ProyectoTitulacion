import { Injectable } from '@angular/core';
import {AngularFireDatabase, AngularFireList} from 'angularfire2/database';
import {Sismo} from '../models/sismo';
@Injectable({
  providedIn: 'root'
})
export class SismoService {

  //Declaración de variables 
  sismoList!: AngularFireList<any>;
  selectedSismo: Sismo = new Sismo();

  //Constructor
  constructor(private fireBase: AngularFireDatabase) { 
  }

  //Método listar sismos
  getSismos(){
    return this.sismoList = this.fireBase.list('sismos');
  }

  //Insertar sismos
  insertSismo(sismo: Sismo){
    this.sismoList.push({
      idEvento: sismo.idEvento,
      magnitud: sismo.magnitud,
      tipo:sismo.tipo,
      horaLocal: sismo.horaLocal,
      latitud: sismo.latitud,
      longitud: sismo.longitud,
      profundidad: sismo.profundidad,
      region: sismo.region,
      ciudadMasCercana: sismo.ciudadMasCercana,
      modo:sismo.modo,
      horaUTC: sismo.horaUTC,
      update: sismo.update
    });
  }

  //Método actualizar sismo
  updateSismo(sismo: Sismo){
    this.sismoList.update(sismo.$key,{
      idEvento: sismo.idEvento,
      magnitud: sismo.magnitud,
      tipo:sismo.tipo,
      horaLocal: sismo.horaLocal,
      latitud: sismo.latitud,
      longitud: sismo.longitud,
      profundidad: sismo.profundidad,
      region: sismo.region,
      ciudadMasCercana: sismo.ciudadMasCercana,
      modo:sismo.modo,
      horaUTC: sismo.horaUTC,
      update: sismo.update
    });
  }

  //Método borrar sismo
  deleteSismo($key:string){
    this.sismoList.remove($key);
  }
}

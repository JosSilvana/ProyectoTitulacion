import { Reporte } from "./reporte";

export class Sismo {
    $key!: string;
    idEvento!: string;
    magnitud!: number;
    tipo!:string;
    horaLocal!: Date;
    latitud!: string;
    longitud!: string;
    profundidad!: number;
    region!: string;
    ciudadMasCercana!: string;
    modo!: string;
    horaUTC!: Date;
    update!: Date; 
    reporteUsuario!:Array<Reporte>; 
}


import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

//Importar servicios
import {SismoService } from '../../../services/sismo.service';
import {ToastrService} from 'ngx-toastr';

//Clases
import {Sismo} from '../../../models/sismo'


@Component({
  selector: 'app-sismo',
  templateUrl: './sismo.component.html',
  styleUrls: ['./sismo.component.scss']
})
export class SismoComponent implements OnInit {

  constructor(public sismoService: SismoService, private toast:ToastrService) { }

  ngOnInit(): void {
    this.sismoService.getSismos();
    this.resetForm();
  }
  //Guardar los datos de sismo
  onSubmit(sismoForm: NgForm){
    if(sismoForm.value.$key == null){
      this.sismoService.insertSismo(sismoForm.value);
      this.toast.success('Successful Operation', 'Sismo Guardado');
    }
    else {
      this.sismoService.updateSismo(sismoForm.value);
      this.toast.success('Successful Operation', 'Sismo Editado');
    }
    this.resetForm(sismoForm);
  }

  //Resetear la variable SelectedSismo en el formulario
  resetForm(sismoForm?: NgForm){
    if(sismoForm != null){
      sismoForm.reset();
      this.sismoService.selectedSismo = new Sismo();
    }
  }
}

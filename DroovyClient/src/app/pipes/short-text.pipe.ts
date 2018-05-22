import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'shortText'})
export class ShortTextPipe implements PipeTransform {
  transform(value: string, exponent: string): string {
    const exp = parseFloat(exponent);
    if (value.length > exp){
      return value.substring(0,exp)+"...";
    }else{
      return value;
    }
  }

}

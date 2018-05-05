import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fileSize'
})
export class FileSizePipe implements PipeTransform {

  transform(size: number): string {

    var unite = ['octets','ko','mo','go'];
    var uniteId = 0;

    while ( size >= 1024 ) {
      size /= 1024;
      uniteId ++;
    }

    return size.toPrecision(2)+" "+unite[uniteId];
  }

}
